package models

import play.api.data.Forms._
import play.api.data._

case class nameForm(newName: String)

// this could be defined somewhere else,
// but I prefer to keep it in the companion object
object nameForm {
  val form: Form[nameForm] = Form(
    mapping(
      "newName" -> nonEmptyText
    )(nameForm.apply)(nameForm.unapply)
  )
}
