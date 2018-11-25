package models

import play.api.data.Forms._
import play.api.data._

case class pwForm(newPW: String)

// this could be defined somewhere else,
// but I prefer to keep it in the companion object
object pwForm {
  val form: Form[pwForm] = Form(
    mapping(
      "newPW" -> nonEmptyText.verifying("Must contain 1 capital letter, 1 lowercase letter, 1 digit, 1 special symbol and be 5 to 10 symbols long",
        pw => pw.matches("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(.{5,10})$"))
    )(pwForm.apply)(pwForm.unapply)
  )
}