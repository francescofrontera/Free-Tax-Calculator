package ffrontera.App

import ffrontera.Reader.Reader
import ffrontera.services.CartService

object SalexTaxCalculator {

  case class Products(in: CartService)

  def read(magent: Reader): Products = ???

}
