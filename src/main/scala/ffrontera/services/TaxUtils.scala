package ffrontera.services

import ffrontera.models.{Item, ProductEnum}
import ffrontera.services.Dsl.TaxResult

object TaxUtils {

  import ffrontera.Utils._

  @inline final val zero: BigDecimal = BigDecimal("0.0")

  @inline private def calculateTax =
    (price: BigDecimal, range: BigDecimal) => price * range

  final def composeCalculationAndScale(price: BigDecimal,
                                       basetax: BigDecimal,
                                       importedTax: BigDecimal,
                                       roundApproximation: BigDecimal,
                                       category: ProductEnum.Category,
                                       isImported: Boolean): BigDecimal = {
    val gTax =
      if (ProductEnum.notTaxCategory(category))
        calculateTax(price, basetax)
      else
        zero

    val iTax =
      if (isImported)
        calculateTax(price, importedTax)
      else
        zero

    (gTax + iTax) roundField roundApproximation
  }

  def calculateTax(items: Seq[Item],
                   taxRange: BigDecimal,
                   importedTaxRange: BigDecimal,
                   roundTax: BigDecimal): TaxResult =
    items
      .foldLeft(TaxResult(List.empty[Item], TaxUtils.zero, TaxUtils.zero)) {
        case (acc, item) =>
          val TaxResult(items, tot, totTax) = acc
          val Item(category, _, price, isImported, qt, _) = item

          val totalClass = TaxUtils.composeCalculationAndScale(
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

          acc.copy(updatedItems, updatedTot, updatedTax)
      }

}
