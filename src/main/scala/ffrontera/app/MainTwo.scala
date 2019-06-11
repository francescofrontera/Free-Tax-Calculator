package ffrontera.app

import ffrontera.builder.TaxCalculator

object MainTwo extends App {
  import ffrontera.reader.Reader._

  val (items, total, totalTax) = TaxCalculator
    .from(
      Seq(
        "book;book;12.49;false;1;02c7a763-e1fe-4e64-b934-cf006bf55616",
        "music;cd;14.99;false;1;02c7a763-e1fe-4e64-b934-cf006bf55617",
        "food;chocolate;0.85;false;1;02c7a763-e1fe-4e64-b934-cf006bf55618"
      ))
    .build
    .calculateTaxForAllProducts

  println(s"ITEMS => \n\t${items.mkString("\n\t")}")
  println(s"TOTAL => $total")
  println(s"TAX => $totalTax")
}
