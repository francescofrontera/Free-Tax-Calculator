package ffrontera.reader

import java.io.{File, FileNotFoundException}
import java.util.UUID

import ffrontera.models.Item
import ffrontera.models.ProductEnum.Book
import org.scalatest.{Matchers, WordSpec}

class ReaderSpec extends WordSpec with Matchers {

  import ffrontera.reader.Reader

  "Reader" should {
    "Read correct csv" in {
      val result = Reader.fromFile(new File(getClass.getResource("/test.csv").getPath))()

      result.size shouldBe 1
      result.head shouldBe Item(
        Book,
        "book",
        BigDecimal(12.49),
        false,
        1,
        UUID.fromString("02c7a763-e1fe-4e64-b934-cf006bf55616"))
    }

    "Error wrong path" in {
      an[FileNotFoundException] should be thrownBy Reader.fromFile(new File("bob"))()
    }

    "Read correct string" in {
      val result = Reader.fromSeq("book;book;12.49;false;1;02c7a763-e1fe-4e64-b934-cf006bf55616" +: Nil)()

      result.size shouldBe 1
      result.head shouldBe Item(
        Book,
        "book",
        BigDecimal(12.49),
        false,
        1,
        UUID.fromString("02c7a763-e1fe-4e64-b934-cf006bf55616"))
    }
  }

}
