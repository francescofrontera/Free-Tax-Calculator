package ffrontera.dsl.tax

import ffrontera.dsl.Dsl.TaxResult
import ffrontera.models.{Item, ProductEnum}

package object utils {
  import ffrontera.Utils._

  private[this] final val zero: BigDecimal = BigDecimal("0.0")

  def calculate(items: Seq[Item],
                taxRange: BigDecimal,
                importedTaxRange: BigDecimal,
                roundTax: BigDecimal): TaxResult =
    items
      .foldLeft(TaxResult(List.empty[Item], zero, zero)) {
        case (acc, item) =>
          val TaxResult(items, tot, totTax) = acc
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

          acc.copy(updatedItems, updatedTot, updatedTax)
      }

  private[this] def priceXRange: (BigDecimal, BigDecimal) ⇒ BigDecimal =
    (price: BigDecimal, range: BigDecimal) => price * range

  private[this] final def composeCalculationAndScale(
      price: BigDecimal,
      basetax: BigDecimal,
      importedTax: BigDecimal,
      roundApproximation: BigDecimal,
      category: ProductEnum.Category,
      isImported: Boolean): BigDecimal = {
    val gTax =
      if (ProductEnum.notTaxCategory(category))
        priceXRange(price, basetax)
      else
        zero

    val iTax =
      if (isImported)
        priceXRange(price, importedTax)
      else
        zero

    (gTax + iTax) roundField roundApproximation
  }
}
