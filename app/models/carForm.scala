package models

import play.api.data.Forms._
import play.api.data._

case class carForm(brand: String, color: String, t: String)

// this could be defined somewhere else,
// but I prefer to keep it in the companion object
object carForm {
  val form: Form[carForm] = Form(
    mapping(
      "brand" -> nonEmptyText,
      "color" -> nonEmptyText,
      "type" -> nonEmptyText
    )(carForm.apply)(carForm.unapply)
  )
}
