package ffrontera.errors



object CommonError {
  trait CartError
  case class NoSuchProductException(msg: String) extends CartError
  case class InvalidQuantityException(msg: String) extends CartError

}
