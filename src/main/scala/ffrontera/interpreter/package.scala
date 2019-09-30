package ffrontera

import ffrontera.services.Dsl
import scalaz.{Free, Monad, ~>}

package object interpreter {
  import Dsl._

  object SalesTaxInterpreter extends ImpureInterpreter

  object Runner {
    implicit class Wrapper[OUT](program: Free[SalesTaxDSL, OUT]) {
      def execute[F[_]](implicit
                        interpreter: SalesTaxDSL ~> F,
                        monad: Monad[F]): F[OUT] = program.foldMap(interpreter)
    }
  }

}
