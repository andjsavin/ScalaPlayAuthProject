package models

import play.api.data.Forms._
import play.api.data._

case class surnameForm(newSurname: String)

// this could be defined somewhere else,
// but I prefer to keep it in the companion object
object surnameForm {
  val form: Form[surnameForm] = Form(
    mapping(
      "newSurname" -> nonEmptyText
    )(surnameForm.apply)(surnameForm.unapply)
  )
}