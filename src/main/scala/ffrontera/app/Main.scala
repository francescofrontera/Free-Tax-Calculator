package ffrontera.app

import java.util.UUID

import ffrontera.interpreter.Runner._
import ffrontera.interpreter.SalesTaxInterpreter
import ffrontera.models.Item
import ffrontera.reader.Reader
import ffrontera.reader.Reader._
import ffrontera.services.Dsl._
import scalaz.{Free, Id, ~>}
import scalaz.std.list._
import scalaz.syntax.traverse._

object Main extends App {
  type Op[A] = Free[SalesTaxDSL, A]

  implicit val executor: SalesTaxDSL ~> Id.Id = SalesTaxInterpreter.impure

  def from(r: Reader): List[Item] = r().toList

  def program: Free[SalesTaxDSL, TaxResult] = {
    //TODO: IMPURE CODE HERE, wrapping on IO
    val asFree = from("test_one.csv") map addProduct

    for {
      _ ← asFree.sequence[Op, UUID]
      taxResult ← calculateTax()
    } yield taxResult
  }


  val TaxResult(items, total, totalTax) = program.execute

  println(s"ITEMS => \n\t${items.mkString("\n\t")}")
  println(s"TOTAL => $total")
  println(s"TAX => $totalTax")
}
