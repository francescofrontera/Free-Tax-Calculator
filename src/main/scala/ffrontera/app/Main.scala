package ffrontera.app

import java.util.UUID

import ffrontera.interpreter.Runner._
import ffrontera.interpreter.SalesTaxInterpreter
import ffrontera.reader.Reader
import ffrontera.reader.Reader._
import ffrontera.services.Dsl._
import scalaz.std.list._
import scalaz.syntax.traverse._
import scalaz.{Free, Id, ~>}

object Main extends App {
  type Op[A] = Free[SalesTaxDSL, A]

  implicit val executor: SalesTaxDSL ~> Id.Id = SalesTaxInterpreter.impure

  def data: List[Free[SalesTaxDSL, UUID]] =
    Reader
      .readData("test_one.csv")
      .map(items ⇒ items.map(addProduct))
      .unsafePerformIO()

  def program: Op[TaxResult] =
    for {
      _ ← data.sequence[Op, UUID]
      result ← calculateTax()
    } yield result

  val TaxResult(items, total, totalTax) = program.execute

  println(s"ITEMS => \n\t${items.mkString("\n\t")}")
  println(s"TOTAL => $total")
  println(s"TAX => $totalTax")
}
