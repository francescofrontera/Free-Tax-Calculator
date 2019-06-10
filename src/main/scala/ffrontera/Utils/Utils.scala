package ffrontera

object Utils {

  implicit class DoubleScaleOps(in: java.math.BigDecimal) {

    def roundField = {
      if (in.signum() == 0) in
      else {
        val rounding = new java.math.BigDecimal("0.05")
        in.divide(rounding, 0, java.math.RoundingMode.UP)
          .multiply(rounding)
      }
    }

  }

}
