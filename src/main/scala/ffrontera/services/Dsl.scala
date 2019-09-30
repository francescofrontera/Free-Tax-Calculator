package ffrontera.services

import java.util.UUID

import ffrontera.errors.CommonError.CartError
import ffrontera.models.Item
import scalaz.Free

object Dsl {

  final case class TaxResult(ls: List[Item], tax: BigDecimal, tax2: BigDecimal)

  sealed trait SalesTaxDSL[V]

  final case class AddProduct(product: Item) extends SalesTaxDSL[UUID]

  final case class RemoveProduct(product: UUID)
    extends SalesTaxDSL[Either[CartError, UUID]]

  final case class GetItem(product: UUID) extends SalesTaxDSL[Option[Item]]

  final case class GetAllProducts() extends SalesTaxDSL[Seq[Item]]

  final case class Tax(taxRange: BigDecimal,
                       importedTaxRange: BigDecimal,
                       roundTax: BigDecimal,
                       allItems: Seq[Item])
    extends SalesTaxDSL[TaxResult]

  final def addProduct(v: Item): Free[SalesTaxDSL, UUID] =
    Free.liftF[SalesTaxDSL, UUID](AddProduct(v))

  final def getProduct(id: UUID): Free[SalesTaxDSL, Option[Item]] =
    Free.liftF[SalesTaxDSL, Option[Item]](GetItem(id))

  final def removeProduct(id: UUID): Free[SalesTaxDSL, Either[CartError, UUID]] =
    Free.liftF[SalesTaxDSL, Either[CartError, UUID]](RemoveProduct(id))

  final def getAllProduct: Free[SalesTaxDSL, Seq[Item]] =
    Free.liftF[SalesTaxDSL, Seq[Item]](GetAllProducts())

  final def calculateTax(
                          taxRange: BigDecimal = BigDecimal("0.10"),
                          importedTaxRange: BigDecimal = BigDecimal("0.05"),
                          roundTax: BigDecimal = BigDecimal("0.05")): Free[SalesTaxDSL, TaxResult] =
    for {
      items ← getAllProduct
      computation ← Free.liftF[SalesTaxDSL, TaxResult](
        Tax(taxRange, importedTaxRange, roundTax, items))
    } yield computation

}
