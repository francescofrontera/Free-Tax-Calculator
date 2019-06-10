package ffrontera.services

import java.util.UUID

import com.typesafe.scalalogging.LazyLogging
import ffrontera.errors.CommonError
import ffrontera.models.Item

import scala.collection.mutable

sealed class CartServiceImpl extends CartService with TaxOps with LazyLogging {
  final val BasicRate = BigDecimal("0.10")
  final val ImportedRate = BigDecimal("0.05")
  final val StartingTot = BigDecimal("0.0")

  private final val cart = mutable.Map.empty[UUID, (Item, Int)]

  private def updateOrRemove(item: Item, quantity: Int): (UUID, Int) = {
    val productId = item.id

    logger.info(s"Remove $quantity for product: $productId from shopping cart")
    val decrementQuantity = quantity - 1

    if (quantity >= 1) cart(productId) = (item, decrementQuantity)
    else cart.remove(productId)

    (productId, decrementQuantity)
  }

  override def addProduct(
      item: Item,
      quantity: Int = 1): Either[CommonError.CartError, (UUID, Int)] = {
    val pId = item.id

    if (quantity >= 1) {
      logger.info(s"added product=$pId, quantity=$quantity")
      cart(pId) = (item, quantity)
      Right((pId, quantity))
    } else {
      logger.error(s"Invalid quantity, caused by $quantity for producut $pId")
      Left(CommonError.InvalidQuantityException(""))
    }
  }

  override def removeProduct(pId: UUID): Either[CommonError.CartError, (UUID, Int)] =
    cart.get(pId) match {
      case Some((product, quantity)) =>
        Right(updateOrRemove(product, quantity))
      case None =>
        Left(
          CommonError.NoSuchProductException(
            s"Not found product with this id: $pId"))
    }

  override def calculateTaxForAllProducts: SalesTaxResult = {
    logger.info("Starting to calculate all tax for cart products...")
    cart.values.toList
      .foldLeft((List.empty[Item], StartingTot, StartingTot)) {
        case (acc, (product, qt)) =>
          val (products, tot, totTaxs) = acc
          val Item(category, _, price, isImported, _) = product

          val totalClass =
            composeCalculationAndScale(price, category, isImported)
          val newPrice = price + totalClass

          (product.copy(price = newPrice) :: products,
           tot + newPrice,
           totTaxs + totalClass)
      }

  }
}
