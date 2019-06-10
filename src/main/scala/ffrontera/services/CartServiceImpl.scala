package ffrontera.services

import java.util.UUID

import com.typesafe.scalalogging.LazyLogging
import ffrontera.errors.CommonError
import ffrontera.models.Item
import ffrontera.services.ops.TaxOps

import scala.collection.mutable

object CartServiceImpl {
  def fromSeq(in: Seq[Item],
              taxRange: BigDecimal = BigDecimal("0.10"),
              importTaxRange: BigDecimal = BigDecimal("0.05")) = {
    val serv = new CartServiceImpl(taxRange = taxRange, importedTaxRange = importTaxRange)
    in.foreach(serv.addProduct)
    serv
  }
}

sealed class CartServiceImpl(taxRange: BigDecimal = BigDecimal("0.10"),
                             importedTaxRange: BigDecimal = BigDecimal("0.05"),
                             roundTax: BigDecimal = BigDecimal("0.05"))
    extends CartService
    with TaxOps
    with LazyLogging {
  private lazy val cart: mutable.Map[UUID, Item] = mutable.Map.empty[UUID, Item]

  private def updateOrRemove(item: Item): UUID = {
    val productId = item.id
    val quantity = item.quantity

    logger.info(s"Remove $quantity for product: $productId from shopping cart")
    val decrementQuantity = quantity - 1

    if (decrementQuantity >= 1)
      cart(productId) = item.copy(quantity = decrementQuantity)
    else cart.remove(productId)

    productId
  }

  @inline def getAllProducts: Seq[Item] = cart.values.toSeq

  @inline def getItem(id: UUID): Option[Item] = cart.get(id)

  def addProduct(item: Item): UUID = {
    val pId = item.id
    cart(pId) = item
    pId
  }

  def removeProduct(pId: UUID): Either[CommonError.CartError, UUID] =
    cart.get(pId) match {
      case Some(product) => Right(updateOrRemove(product))
      case None =>
        Left(
          CommonError.NoSuchProductException(
            s"Not found product with this id: $pId"))
    }

  def calculateTaxForAllProducts: SalesTaxResult = {
    logger.info("Starting to calculate all tax for cart products...")
    val itemsAsSeq = cart.values.toSeq

    itemsAsSeq.foldLeft((List.empty[Item], zero, zero)) {
      case (acc, item) =>
        val (items, tot, totTax) = acc
        val Item(category, _, price, isImported, qt, _) = item

        val totalClass = composeCalculationAndScale(
          price,
          taxRange,
          importedTaxRange,
          roundTax,
          category,
          isImported
        )

        val newPrice = price + totalClass
        val updatedItems = item.copy(price = newPrice) :: items
        val updatedTot = tot + (newPrice * qt)
        val updatedTax = totTax + (totalClass * qt)

        (updatedItems, updatedTot, updatedTax)
    }

  }
}
