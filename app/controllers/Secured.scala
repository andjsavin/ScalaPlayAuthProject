package controllers

import play.api.mvc._
import models.User
import play.api.i18n.I18nSupport
import service.userService

/**
  * Provide security features
  */
trait Secured extends Controller with I18nSupport {

  /**
    * Retrieve the connected user's email
    */
  private def username(request: RequestHeader) = request.session.get("username")

  /**
    * Not authorized, forward to login
    */
  private def onUnauthorized(request: RequestHeader) = {
    Results.Redirect(routes.Authentication.index)
  }

  /**
    * Action for authenticated users.
    */
  def IsAuthenticated(f: => String => Request[AnyContent] => Result) = {
    Security.Authenticated(username, onUnauthorized) { user =>
      Action(request => f(user)(request))
    }
  }
}