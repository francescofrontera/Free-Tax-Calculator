package ffrontera.services

import java.util.UUID

import ffrontera.errors.CommonError
import ffrontera.models.Item

trait CartService {
  type SalesTaxResult = (List[Item], BigDecimal, BigDecimal)

  def addProduct(p: Item, quantity: Int): Either[CommonError.CartError, (UUID, Int)]
  def removeProduct(pId: UUID): Either[CommonError.CartError, (UUID, Int)]

  def calculateTaxForAllProducts: SalesTaxResult
}
