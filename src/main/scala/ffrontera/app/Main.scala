package ffrontera.app

import java.io.File

import ffrontera.builder.TaxCalculator

object Main extends App {
  import ffrontera.Reader.Reader._

  val (items, total, totalTax) = TaxCalculator
    .from(new File(getClass.getResource("/test_one.csv").getPath))
    .build
    .calculateTaxForAllProducts

  println(s"ITEMS => \n\t${items.mkString("\n\t")}")
  println(s"TOTAL => $total")
  println(s"TAX => $totalTax")
}
