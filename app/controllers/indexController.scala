package controllers

import com.google.inject.Inject
import models.{User, regForm, userForm}
import play.api.data.Forms._
import play.api.data._
import play.api.mvc.{Action, Controller}
import service.userService

import scala.concurrent.{Await, Future}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.duration.Duration

class indexController @Inject()
(userService: userService,
 val messagesApi: MessagesApi) extends Controller with I18nSupport {

  def userRegister = Action.async { implicit  request =>
    userService.listAllUsers map { users =>
      Ok(views.html.register(regForm.form,users))
    }
  }

  def addUser() = Action.async { implicit request =>
    regForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(Ok(views.html.register(errorForm,Seq.empty[User]))),
      data => {
        val newUser = User(0,data.username, data.password, data.name, data.surname, 0)
        val users = Await.result(userService.listAllUsers, Duration.Inf)
        var unique = 0
        for (user <- users) {
          if (user.username == data.username) {
            unique = 1
          }
        }
        if (unique == 1) {
          userService.listAllUsers map { users =>
            Ok(views.html.forbidden())
          }
        } else {
          userService.addUser(newUser).map(res =>
            Redirect(routes.Authentication.index()).flashing(Messages("flash.success") -> res)
          )
        }
      })
  }
}