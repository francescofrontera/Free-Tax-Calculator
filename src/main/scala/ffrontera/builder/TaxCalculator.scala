package ffrontera.builder

import ffrontera.reader.Reader
import ffrontera.models.Item
import ffrontera.services.{CartService, CartServiceImpl}

object TaxCalculator {
  case class TaxCalBuilder(items: Seq[Item] = Nil,
                           taxRange: BigDecimal = BigDecimal("0.10"),
                           importTaxRange: BigDecimal = BigDecimal("0.05")) {

    def setTaxRange(taxRangeVal: String): TaxCalBuilder =
      this.copy(taxRange = BigDecimal(taxRangeVal))

    def setImportTaxRange(taxImportRange: String): TaxCalBuilder =
      this.copy(importTaxRange = BigDecimal(taxImportRange))

    @inline def build: CartService = CartServiceImpl.fromSeq(items, taxRange, importTaxRange)
  }

  def from(magnetReader: Reader): TaxCalBuilder = {
    val readData = magnetReader()
    TaxCalBuilder(items = readData)
  }

  def apply(): TaxCalBuilder = new TaxCalBuilder()
}
