package ffrontera.models

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

  def fromString(in: String): Category = in.toLowerCase match {
    case "food" => Food
    case "book" => Book
    case "medical" => Medical
    case _ => Other
  }

}
