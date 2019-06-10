package ffrontera.models

import java.util.UUID

import ffrontera.models.ProductEnum.Category

object Item {
  def apply(category: Category,
            name: String,
            price: String,
            isImported: Boolean = false,
            qt: Int = 1,
            id: UUID = UUID.randomUUID()): Item =
    new Item(category, name, BigDecimal(price), isImported, qt, id)
}

sealed case class Item(category: ProductEnum.Category,
                       name: String,
                       price: BigDecimal,
                       isImported: Boolean,
                       quantity: Int,
                       id: UUID)
