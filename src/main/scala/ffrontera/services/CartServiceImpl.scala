package ffrontera.services

import java.util.UUID

import com.typesafe.scalalogging.LazyLogging
import ffrontera.errors.CommonError
import ffrontera.models.Item

import scala.collection.mutable

sealed class CartServiceImpl(taxRange: BigDecimal = BigDecimal("0.10"),
                             importedTaxRange: BigDecimal = BigDecimal("0.05"),
                             roundTax: BigDecimal = BigDecimal("0.05"))
    extends CartService
    with TaxOps
    with LazyLogging {
  private final val cart: mutable.Map[UUID, Item] = mutable.Map.empty[UUID, Item]

  private def updateOrRemove(item: Item): UUID = {
    val productId = item.id
    val quantity = item.quantity

    logger.info(s"Remove $quantity for product: $productId from shopping cart")
    val decrementQuantity = quantity - 1

    if (quantity >= 1) cart(productId) = item.copy(quantity = decrementQuantity)
    else cart.remove(productId)

    productId
  }

  @inline def getAllProducts: Seq[Item] = cart.values.toSeq

  @inline def getItem(id: UUID): Option[Item] = cart.get(id)

  def addProduct(item: Item): Either[CommonError.CartError, UUID] = {
    val pId = item.id
    val qt = item.quantity
    if (qt >= 1) {
      logger.info(s"added product=$pId, quantity=$qt")
      cart(pId) = item
      Right(pId)
    } else {
      logger.error(s"Invalid quantity, caused by $qt for producut $pId")
      Left(CommonError.InvalidQuantityException(""))
    }
  }

  def removeProduct(pId: UUID): Either[CommonError.CartError, UUID] =
    cart.get(pId) match {
      case Some(product) =>
        Right(updateOrRemove(product))
      case None =>
        Left(
          CommonError.NoSuchProductException(
            s"Not found product with this id: $pId"))
    }

  override def calculateTaxForAllProducts: SalesTaxResult = {
    logger.info("Starting to calculate all tax for cart products...")
    cart.values.toList
      .foldLeft((List.empty[Item], zero, zero)) {
        case (acc, product) =>
          val (products, tot, totTaxs) = acc
          val Item(category, _, price, isImported, qt, _) = product

          val totalClass = composeCalculationAndScale(
            price,
            taxRange,
            importedTaxRange,
            roundTax,
            category,
            isImported
          )

          val newPrice = (price + totalClass) * qt

          (product.copy(price = newPrice) :: products,
           tot + newPrice,
           totTaxs + (totalClass * qt))
      }

  }
}
