//package ffrontera.builder
//
//import ffrontera.services.CartService
//import org.scalatest.{Matchers, WordSpec}
//
//class TaxCalculatorSpec extends WordSpec with Matchers {
//  "TaxCalculator" should {
//    "Build an instance of CartService" in {
//      val t: TaxCalculator.TaxCalBuilder =
//        TaxCalculator()
//          .setImportTaxRange("0.02")
//          .setTaxRange("0.01")
//
//      t shouldBe TaxCalculator.TaxCalBuilder(Nil, BigDecimal("0.01"), BigDecimal("0.02"))
//      t.build shouldBe a[CartService]
//    }
//  }
//
//}
