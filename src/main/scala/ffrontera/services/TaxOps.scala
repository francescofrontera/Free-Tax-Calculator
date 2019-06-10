package ffrontera.services

import ffrontera.models.ProductEnum

trait TaxOps { _: CartServiceImpl =>
  import ffrontera.Utils._

  private def calculateTax =
    (price: BigDecimal, category: ProductEnum.Category) =>
      if (ProductEnum.notTaxCategory(category)) price * BasicRate
      else StartingTot

  private def calculateImportedTax =
    (price: BigDecimal, isImported: Boolean) =>
      if (isImported) price * ImportedRate
      else StartingTot

  protected def composeCalculationAndScale(price: BigDecimal,
                                           category: ProductEnum.Category,
                                           isImported: Boolean) = {
    val cTax = calculateTax(price, category)
    val iTax = calculateImportedTax(price, isImported)

    (cTax + iTax) roundField ImportedRate
  }

}
