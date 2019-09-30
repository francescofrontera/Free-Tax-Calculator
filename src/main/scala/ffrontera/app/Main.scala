package ffrontera.app

import java.util.UUID

import ffrontera.interpreter.Runner._
import ffrontera.interpreter.SalesTaxInterpreter
import ffrontera.reader.Reader
import ffrontera.reader.Reader._
import ffrontera.services.Dsl._
import scalaz.Free
import scalaz.effect.IO
import scalaz.std.list._
import scalaz.syntax.traverse._

object Main extends App {
  type Op[A] = Free[SalesTaxDSL, A]

  implicit val executor = SalesTaxInterpreter.impure

  def program: IO[Op[TaxResult]] =
    for {
      read ← Reader.readData("test_one.csv").map(items ⇒ items.map(addProduct))
      result ← IO(read.sequence[Op, UUID].flatMap(_ ⇒ calculateTax()))
    } yield result

  val TaxResult(items, total, totalTax) =
    program
      .map(_.execute)
      .unsafePerformIO()

  println(s"ITEMS => \n\t${items.mkString("\n\t")}")
  println(s"TOTAL => $total")
  println(s"TAX => $totalTax")
}
