package ffrontera.reader

import java.util.UUID

import ffrontera.models.{Item, ProductEnum}
import scalaz.effect._

import scala.util.Try

trait Reader {
  def apply(): IO[List[Item]]
}

object Reader {
  private[this] def splittingLogic(lines: String,
                                   delimiter: String = ";"): Array[String] =
    lines.split(delimiter)

  private[this] def arrayAsItem(in: Array[String]): Item = in match {
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
      Try(scala.io.Source.fromResource(in)).fold(
        IO.throwIO,
        bufferedSource ⇒
          IO {
            val lines = bufferedSource
              .getLines()
              .drop(1)
              .map(line => splittingLogic(line))

            lines.map(arrayAsItem).toList
          }
      )

  implicit def fromSeq(in: List[String]): Reader =
    () =>
      IO {
        in.map { line =>
          arrayAsItem(splittingLogic(line))
        }
      }

  def readData(reader: Reader): IO[List[Item]] = reader()
}
