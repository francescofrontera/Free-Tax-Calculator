package ffrontera.app

import java.util.UUID

import ffrontera.interpreter.Runner._
import ffrontera.interpreter.SalesTaxInterpreter
import ffrontera.reader.Reader, Reader._
import ffrontera.dsl.Dsl._
import scalaz.std.list._
import scalaz.syntax.traverse._
import scalaz.{Free, Id, ~>}

object Main extends App {
  type ProgramResult[A] = Free[SalesTaxDSL, A]

  implicit val executor: SalesTaxDSL ~> Id.Id = SalesTaxInterpreter.impure

  def data: List[Free[SalesTaxDSL, UUID]] =
    Reader
      .readData("test_one.csv")
      .map(items ⇒ items.map(addProduct))
      .unsafePerformIO()

  //We could make all program with IO..
  def program: ProgramResult[TaxResult] =
    for {
      _ ← data.sequence[ProgramResult, UUID]
      result ← calculateTax()
    } yield result

  val TaxResult(items, total, totalTax) = program.execute

  println(s"ITEMS => \n\t${items.mkString("\n\t")}")
  println(s"TOTAL => $total")
  println(s"TAX => $totalTax")
}
