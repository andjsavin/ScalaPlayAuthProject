package controllers

import javax.inject.Inject
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.i18n.MessagesApi
import service.userService

class Authentication @Inject()
(userService: userService,
 val messagesApi: MessagesApi) extends Controller {

  val loginForm = Form(
    tuple(
      "username" -> text,
      "password" -> text
    ).verifying ("Invalid email or password", result => result match {
      case (username, password) => userService.auth(username, password).isDefined
    })
  )

  /**
    * Login page.
    */


  def index = Action { implicit request =>
    Ok(views.html.index(loginForm))
  }

  /**
    * Logout and clean the session.
    */
  def logout = Action {
    Redirect(routes.Authentication.index()).withNewSession.flashing(
      "success" -> "You've been logged out"
    )
  }

  /**
    * Handle login form submission.
    */
  def auth = Action { implicit request =>
    loginForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.index(formWithErrors)),
      user => Redirect(routes.Restricted.index()).withSession("username" -> user._1)
    )
  }

}