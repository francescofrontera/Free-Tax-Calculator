package ffrontera.services

import java.util.UUID

import com.typesafe.scalalogging.LazyLogging
import ffrontera.Utils
import ffrontera.errors.CommonError
import ffrontera.models.{Item, ProductEnum}

import scala.collection.mutable

sealed class CartServiceImpl extends CartService with LazyLogging {
  import Utils._

  final val BasicRate = new java.math.BigDecimal("0.10")
  final val ImportedRate = new java.math.BigDecimal("0.05")

  private final val cart = mutable.Map.empty[UUID, (Item, Int)]

  private def updateOrRemove(p: Item, quantity: Int): (UUID, Int) = {
    val productId = p.id

    logger.info(s"Remove $quantity for product: $productId from shopping cart")
    val decrementQuantity = quantity - 1

    if (quantity >= 1) cart(productId) = (p, decrementQuantity)
    else cart.remove(productId)

    (productId, decrementQuantity)
  }

  override def addProduct(item: Item, quantity: Int = 1): Either[CommonError.CartError, (UUID, Int)] = {
    val pId = item.id

    logger.info(s"added product=$pId, quantity=$quantity")
    if (quantity >= 1) {
      cart(pId) = (item, quantity)
      Right((pId, quantity))
    } else {
      logger.error(s"Invalid quantity, caused by $quantity for producut $pId")
      Left(CommonError.InvalidQuantityException(""))
    }
  }

  override def removeProduct(pId: UUID): Either[CommonError.CartError, (UUID, Int)] = {
    val getProduct = cart.get(pId)

    val result = getProduct map { pWithQuantity =>
      val (product, quantity) = pWithQuantity
      Right(updateOrRemove(product, quantity))
    }

    result.getOrElse(
      Left(
        CommonError.NoSuchProductException(
          s"Not found product with this id: $pId"))
    )
  }

  override def calculateTaxForAllProducts: SalesTaxResult = {
    logger.info("Starting to calculate all tax for cart products...")
    cart.values.toList
      .foldLeft(
        (List.empty[Item],
         new java.math.BigDecimal("0.0"),
         new java.math.BigDecimal("0.0"))) {
        case (acc, (product, qt)) =>
          val (products, tot, totTaxs) = acc
          val Item(category, _, price, isImported, _) = product

          //FIXME: USING FUNCTION
          val calculateSalesTax =
            if (ProductEnum.notTaxCategory(category)) price.multiply(BasicRate)
            else new java.math.BigDecimal("0.0")

          val calculateImportedTax =
            if (isImported) price.multiply(ImportedRate)
            else new java.math.BigDecimal("0.0")

          val totalClass = calculateSalesTax.add(calculateImportedTax).roundField

          val newPrice = price.add(totalClass)

          (product.copy(price = newPrice) :: products,
           tot.add(newPrice),
           (totTaxs.add(totalClass)))
      }

  }
}
