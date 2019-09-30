package ffrontera.reader

import java.util.UUID

import ffrontera.models.{Item, ProductEnum}
import scalaz.effect._

trait Reader {
  def apply(): IO[List[Item]]
}

object Reader {
  @inline private def splittingLogic(lines: String, delimiter: String = ";") =
    lines.split(delimiter)

  private def arrayAsItem(in: Array[String]): Item = in match {
    case Array(category, name, price, isImported, quantity, id) =>
      Item(ProductEnum.fromString(category),
        name,
        price,
        isImported.toBoolean,
        quantity.toInt,
        UUID.fromString(id))
  }

  implicit def fromFile(in: String): Reader =
    () =>
      IO {
        val lines = scala.io.Source
          .fromResource(in)
          .getLines()
          .drop(1)
          .map(line => splittingLogic(line))

        lines.map(arrayAsItem).toList
      }

  implicit def fromSeq(in: List[String]): Reader =
    () =>
      IO {
        in.map { line =>
          arrayAsItem(splittingLogic(line))
        }
      }


  def readData(reader: Reader): IO[List[Item]] = reader()
}
