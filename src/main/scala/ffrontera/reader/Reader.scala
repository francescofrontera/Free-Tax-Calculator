package ffrontera.reader

import java.io.File
import java.util.UUID

import ffrontera.models.{Item, ProductEnum}

trait Reader {
  def apply(): Seq[Item]
}

object Reader {
  @inline private def splittingLogic(lines: String, delimiter: String = ";") =
    lines.split(delimiter)

  private def arrayAsItem(in: Array[String]) = in match {
    case Array(category, name, price, isImported, quantity, id) =>
      Item(ProductEnum.fromString(category),
           name,
           price,
           isImported.toBoolean,
           quantity.toInt,
           UUID.fromString(id))
  }

  implicit def fromFile(in: File) = new Reader {
    override def apply(): Seq[Item] = {
      val lines = io.Source
        .fromFile(in)
        .getLines()
        .drop(1)
        .map(line => splittingLogic(line))

      lines.map(arrayAsItem).toSeq
    }

  }

  implicit def fromSeq(in: Seq[String]) = new Reader {
    override def apply(): Seq[Item] = {
      in.map { line =>
        arrayAsItem(splittingLogic(line))
      }
    }
  }
}
