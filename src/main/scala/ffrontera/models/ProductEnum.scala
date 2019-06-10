package ffrontera.models

import java.util.UUID

import ffrontera.models.ProductEnum.Category

object ProductEnum {
  sealed trait Category

  case object Food extends Category
  case object Book extends Category
  case object Medical extends Category

  case object Other extends Category

  def notApplicableTaxCategory: List[Category] =
    Food :: Book :: Medical :: Nil

  def notTaxCategory(category: Category): Boolean =
    !notApplicableTaxCategory.contains(category)

}

object Item {
  def apply(category: Category,
            name: String,
            price: String,
            isImported: Boolean = false,
            id: UUID = UUID.randomUUID()): Item =
    new Item(category, name, BigDecimal(price), isImported, id)
}
sealed case class Item(category: ProductEnum.Category,
                       name: String,
                       price: BigDecimal,
                       isImported: Boolean,
                       id: UUID)
