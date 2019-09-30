package ffrontera

import scala.math.BigDecimal.RoundingMode

object Utils {

  implicit class BigDecimalOps(in: BigDecimal) {
    def roundField(scaleUP: BigDecimal): BigDecimal = in.signum match {
      case 0 => in
      case _ => ((in / scaleUP).setScale(0, RoundingMode.UP)) * scaleUP
    }
  }

}
