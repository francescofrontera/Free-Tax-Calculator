package ffrontera.app

import java.io.File
import java.util.UUID

import ffrontera.interpeter.Runner
import ffrontera.models.{Item, ProductEnum}
import ffrontera.reader.Reader
import scalaz.{Free, Semigroup}
//
//import java.io.File
//
//import ffrontera.builder.TaxCalculator
//
//object Main extends App {
//  import ffrontera.reader.Reader._
//
//  val (items, total, totalTax) = TaxCalculator
//    .from(new File(getClass.getResource("/test_one.csv").getPath))
//    .build
//    .calculateTaxForAllProducts
//
//  println(s"ITEMS => \n\t${items.mkString("\n\t")}")
//  println(s"TOTAL => $total")
//  println(s"TAX => $totalTax")
//}

object Main extends App {
  import ffrontera.services.Dsl._
  import scalaz.std.list._
  import scalaz.syntax.traverse._

  def from(items: Reader) = items()

  implicit def asSemigroup = new Semigroup[Item] {
    override def append(f1: Item, f2: ⇒ Item): Item = f1
  }

  //from(new File(getClass.getResource("/test_one.csv").getPath)).map(addProduct).sequence[Item, ?]
  def program: Free[SalesTaxDSL, TaxResult] = {
    for {
      _ ← addProduct(Item(ProductEnum.Medical, "Tachipirina", "10.99"))
      _ ← addProduct(Item(ProductEnum.Medical, "Tachipirina", "10.99"))
      taxResult ← calculateTax()
    } yield taxResult
  }

  val result: TaxResult = Runner.impureRunner(program)
  println(s"SHOW THE RESULT ${result}")
}
