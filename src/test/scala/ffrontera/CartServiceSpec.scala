package ffrontera

import ffrontera.models.{Item, ProductEnum}
import ffrontera.services.CartServiceImpl
import org.scalatest.{Matchers, WordSpec}

//FIXME: testing produced list + ADD
class CartServiceSpec extends WordSpec with Matchers {
  "CartService" should {
    "Calculate correct output" in {
      val cartService = new CartServiceImpl()

      cartService.addProduct(Item(ProductEnum.Book, "book", "12.49"))

      cartService.addProduct(Item(ProductEnum.Other, "music cd", "14.99"))
      cartService.addProduct(Item(ProductEnum.Food, "chocolate bar", "0.85"))

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
      cartService.addProduct(
        Item(ProductEnum.Medical, "pillows", "9.75"))
      cartService.addProduct(
        Item(ProductEnum.Food, "chocolate", "11.25", true))

      val (_, tot, totTax) = cartService.calculateTaxForAllProducts
      tot shouldBe 74.68
      totTax shouldBe 6.70
    }
  }

}
