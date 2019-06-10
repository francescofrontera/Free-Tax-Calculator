package ffrontera.services

import java.util.UUID

import ffrontera.errors.CommonError
import ffrontera.models.{Item, ProductEnum}
import org.scalatest.{Matchers, WordSpec}

class CartServiceSpec extends WordSpec with Matchers {
  private val bookId = UUID.randomUUID()
  private val musicCdId = UUID.randomUUID()
  private val foodProductID = UUID.randomUUID()

  "CartService" should {

    "Service operation" should {
      "Remove item" in {
        val cartService = new CartServiceImpl()

        cartService.addProduct(
          Item(ProductEnum.Book, "book", "12.49", false, 10, id = bookId))

        cartService.removeProduct(bookId)
        cartService.getItem(bookId).head.quantity shouldBe 9
      }

      "populate cart from seq" in {
        val p1 = Item(ProductEnum.Book, "book", "12.49", false, id = bookId)
        val p2 =
          Item(ProductEnum.Other, "music cd", "14.99", false, id = musicCdId)
        val p3 = Item(ProductEnum.Food,
                      "chocolate bar",
                      "0.85",
                      false,
                      id = foodProductID)

        val service = CartServiceImpl.fromSeq(Seq(p1, p2, p3))
        service.getAllProducts.size shouldBe 3
      }

      "remove product that doesn't exist cause Error" in {
        val cartService = new CartServiceImpl()
        val uuid = UUID.randomUUID()
        cartService.removeProduct(uuid) shouldBe Left(
          CommonError.NoSuchProductException(
            s"Not found product with this id: $uuid"))
      }
    }

    "Tax operations" should {
      "Calculate correct output" in {
        val cartService = new CartServiceImpl()

        val p1 = cartService.addProduct(
          Item(ProductEnum.Book, "book", "12.49", false, id = bookId))
        val p2 = cartService.addProduct(
          Item(ProductEnum.Other, "music cd", "14.99", false, id = musicCdId))
        val p3 = cartService.addProduct(
          Item(ProductEnum.Food,
               "chocolate bar",
               "0.85",
               false,
               id = foodProductID))

        val (_, tot, totTax) = cartService.calculateTaxForAllProducts
        tot shouldBe 29.83
        totTax shouldBe 1.50
      }

      "Calculate correct output with imported" in {
        val cartService = new CartServiceImpl()

        cartService.addProduct(
          Item(ProductEnum.Food, "box of chocolate", "10.00", true))

        cartService.addProduct(
          Item(ProductEnum.Other, "bottle of perfume", "47.50", true))

        val (_, tot, totTax) = cartService.calculateTaxForAllProducts
        tot shouldBe 65.15
        totTax shouldBe 7.65
      }

      "Calculate correct output with imported and not" in {
        val cartService = new CartServiceImpl()
        cartService.addProduct(
          Item(ProductEnum.Other, "bottle of perfume", "27.99", true))
        cartService.addProduct(
          Item(ProductEnum.Other, "bottle of perfume", "18.99"))
        cartService.addProduct(Item(ProductEnum.Medical, "pillows", "9.75"))
        cartService.addProduct(
          Item(ProductEnum.Food, "chocolate", "11.25", true))

        val (_, tot, totTax) = cartService.calculateTaxForAllProducts
        tot shouldBe 74.68
        totTax shouldBe 6.70
      }
    }

  }

}
