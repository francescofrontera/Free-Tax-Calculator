package ffrontera.Reader

import java.io.File

import ffrontera.models.Item

//INTRODUCING READER
trait Reader {
  def apply(): Seq[Item]
}

object Reader {

  implicit def fromFile(in: File) = new Reader {

    override def apply(): Seq[Item] =
      //ReadFromCsHere
      ???
  }

  implicit def fromSeq(in: Seq[String]) = new Reader {
    override def apply(): Seq[Item] = ???
  }
}
