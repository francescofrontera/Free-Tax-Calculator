package ffrontera.errors

object CommonError {
  trait CartError {
    def msg: String
  }

  case class NoSuchProductException(msg: String) extends CartError
}
