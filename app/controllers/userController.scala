package controllers

import com.google.inject.Inject
import models._
import play.api.data._
import play.api.data.Forms._
import play.api.mvc.{Action, Controller}
import service._

import scala.concurrent.{Await, Future}
import play.api.i18n.{I18nSupport, Messages, MessagesApi}
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.duration.Duration

class userController @Inject()
(userService: userService, carService: carService,
 val messagesApi: MessagesApi) extends Controller with I18nSupport {

  val loginForm = Form(
    tuple(
      "username" -> text,
      "password" -> text
    ).verifying ("Invalid email or password", result => result match {
      case (username, password) => userService.auth(username, password).isDefined
    })
  )

  def adminRegister = Action.async { implicit  request =>
    if (request.session.get("username").isEmpty || userService.getUser(request.session.get("username").get).isEmpty) {
      userService.listAllUsers map { users =>
        Ok(views.html.forbidden("Access only for admins"))
      }
    } else {
      val user = userService.getUser(request.session.get("username").get).get
      val id = userService.getId(request.session.get("username").get)
      if (user.isAdmin != 1) {
        userService.listAllUsers map { users =>
          Ok(views.html.forbidden("Access only for admins"))
        }
      } else {
      userService.listAllUsers map { users =>
        Ok(views.html.user(user, userForm.form, users))
      }
    }
    }
  }

  def addUser() = Action.async { implicit request =>
    val user = userService.getUser(request.session.get("username").get).get
    val id = userService.getId(request.session.get("username").get)
    userForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(Ok(views.html.user(user, errorForm,Seq.empty[User]))),
      data => {
        val newUser = User(0,data.username, data.password, data.name, data.surname, data.isAdmin)
        val users = Await.result(userService.listAllUsers, Duration.Inf)
        var unique = 0
        for (user <- users) {
          if (user.username == data.username) {
            unique = 1
          }
        }
        if (unique == 1) {
          userService.listAllUsers map { users =>
            Ok(views.html.forbidden("User already exists"))
          }
        } else {
          userService.addUser(newUser).map(res =>
            Redirect(routes.userController.adminRegister()).flashing(Messages("flash.success") -> res)
          )
        }
      })
  }

  def deleteUser(id : Int) = Action.async { implicit request =>
    Await.result(carService.deleteUserCars(id), Duration.Inf)
    if (request.session.get("username").isEmpty || userService.getUser(request.session.get("username").get).isEmpty) {
      userService.listAllUsers map { users =>
        Ok(views.html.forbidden("Access only for admins"))
      }
    } else {
      val user = userService.getUser(request.session.get("username").get).get
      val id = userService.getId(request.session.get("username").get)
      if (user.isAdmin != 1) {
        userService.listAllUsers map { users =>
          Ok(views.html.forbidden("Access only for admins"))
        }
      } else {
        userService.deleteUser(id) map { res =>
          Redirect(routes.userController.adminRegister())
        }
      }
    }
  }

}