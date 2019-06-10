package ffrontera.services

import java.util.UUID

import ffrontera.errors.CommonError
import ffrontera.models.Item

trait CartService {
  type SalesTaxResult = (List[Item], BigDecimal, BigDecimal)

  def addProduct(p: Item): Either[CommonError.CartError, UUID]
  def removeProduct(pId: UUID): Either[CommonError.CartError, UUID]

  def getItem(id: UUID): Option[Item]
  def getAllProducts: Seq[Item]

  def calculateTaxForAllProducts: SalesTaxResult
}
