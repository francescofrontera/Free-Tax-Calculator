package ffrontera.models

import java.util.UUID

import ffrontera.models.ProductEnum.Category

sealed case class Item(category: ProductEnum.Category,
                       name: String,
                       price: BigDecimal,
                       isImported: Boolean,
                       id: UUID)

object Item {
  def apply(category: Category,
            name: String,
            price: String,
            isImported: Boolean = false,
            id: UUID = UUID.randomUUID()): Item =
    new Item(category, name, BigDecimal(price), isImported, id)

  def apply(category: Category,
            name: String,
            price: Double,
            isImported: Boolean,
            id: UUID): Item =
    new Item(category, name, BigDecimal(price), isImported, id)
}
