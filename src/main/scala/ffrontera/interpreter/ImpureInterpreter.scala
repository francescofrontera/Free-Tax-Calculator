package ffrontera.interpreter

import java.util.UUID

import ffrontera.errors.CommonError.NoSuchProductException
import ffrontera.models.Item
import ffrontera.services.Dsl._
import ffrontera.services.TaxUtils
import scalaz.Id.Id
import scalaz.{Id, ~>}

trait ImpureInterpreter {
  final def impure: SalesTaxDSL ~> Id.Id =
    new (SalesTaxDSL ~> Id) {
      val state = collection.mutable.Map.empty[UUID, Item]

      override def apply[A](fa: SalesTaxDSL[A]): Id[A] = fa match {
        case AddProduct(item) ⇒
          val id = item.id
          state(id) = item
          id

        case GetItem(uuid) ⇒ state.get(uuid)

        case RemoveProduct(uuid) ⇒
          state get uuid match {
            case Some(uuidRe) ⇒
              state -= uuidRe.id;
              Right(uuid)
            case None ⇒
              Left(
                NoSuchProductException(
                  s"Not found product with this id: $uuid to delete"))
          }

        case GetAllProducts() ⇒ state.values.toSeq

        case Tax(range, imRange, rTax, items) ⇒
          TaxUtils.calculateTax(items, range, imRange, rTax)
      }
    }

}
