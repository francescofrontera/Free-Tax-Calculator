package ffrontera.errors

object CommonError {

  trait CartError {
    def msg: String
  }

  final case class NoSuchProductException(msg: String) extends CartError

}
