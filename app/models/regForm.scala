package models

import play.api.data.Forms._
import play.api.data._

case class regForm(username: String, password: String, name: String, surname: String)

// this could be defined somewhere else,
// but I prefer to keep it in the companion object
object regForm {
  val form: Form[regForm] = Form(
    mapping(
      "username" -> nonEmptyText,
      "password" -> nonEmptyText.verifying("Must contain 1 capital letter, 1 lowercase letter, 1 digit, 1 special symbol and be 5 to 10 symbols long",
        pw => pw.matches("(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*\\W)(.{5,10})$")),
      "name" -> nonEmptyText,
      "surname" -> nonEmptyText
    )(regForm.apply)(regForm.unapply)
  )
}
