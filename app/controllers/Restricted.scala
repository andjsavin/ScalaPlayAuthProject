package controllers

import javax.inject.Inject
import play.api.i18n.{Messages, MessagesApi}
import play.api.mvc.{Action, Controller}
import service.userService
import service.carService
import play.api.mvc._

import scala.concurrent.Future
import models._
import play.api.libs.concurrent.Execution.Implicits._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

class Restricted @Inject()
(userService: userService, carService: carService,
 val messagesApi: MessagesApi) extends Controller with Secured {

  /**
    * Display restricted area only if user is logged in.
    */
  def index = IsAuthenticated { username =>
    _ => userService.getUser(username).map { user =>
      val cars = Await.result(carService.listAllCars(userService.getId(username)), Duration.Inf)
      Ok(views.html.restricted(carForm.form, user, cars, nameForm.form, surnameForm.form, pwForm.form))
    }.getOrElse(Forbidden)
  }

  def addCar() = Action.async { implicit request =>
    val user = userService.getUser(request.session.get("username").get).get
    val id = userService.getId(request.session.get("username").get)
    val cars = Await.result(carService.listAllCars(userService.getId(request.session.get("username").get)), Duration.Inf)
    carForm.form.bindFromRequest.fold(
      // if any error in submitted data
      errorForm => Future.successful(Ok(views.html.restricted(errorForm, user, cars, nameForm.form, surnameForm.form, pwForm.form))),
      data => {
        val newCar = car(0,data.brand, data.color, data.t, id)
        carService.addCar(newCar).map(res =>
          Redirect(routes.Restricted.index()).flashing(Messages("flash.success") -> res)
        )
      })
  }

  def deleteCar(id : Int) = Action.async { implicit request =>
    carService.deleteCar(id) map { res =>
      Redirect(routes.Restricted.index())
    }
  }

  def changeName(id: Int) = Action { implicit request =>
    val user = userService.getUser(request.session.get("username").get).get
    val id = userService.getId(request.session.get("username").get)
    val cars = Await.result(carService.listAllCars(userService.getId(request.session.get("username").get)), Duration.Inf)
    nameForm.form.bindFromRequest.fold(
      errorForm => BadRequest(views.html.restricted(carForm.form, user, cars, errorForm, surnameForm.form, pwForm.form)),
      data => {
        userService.changeName(id, data.newName)
        Redirect(routes.Authentication.index())
      }
    )
  }

  def changeSurname(id: Int) = Action { implicit request =>
    val user = userService.getUser(request.session.get("username").get).get
    val id = userService.getId(request.session.get("username").get)
    val cars = Await.result(carService.listAllCars(userService.getId(request.session.get("username").get)), Duration.Inf)
    surnameForm.form.bindFromRequest.fold(
      errorForm =>
        BadRequest(views.html.restricted(carForm.form, user, cars, nameForm.form, errorForm, pwForm.form)),
      data => {
        userService.changeSurname(id, data.newSurname)
        Redirect(routes.Authentication.index())
      }
    )
  }

  def changePW(id: Int) = Action { implicit request =>
    val user = userService.getUser(request.session.get("username").get).get
    val id = userService.getId(request.session.get("username").get)
    val cars = Await.result(carService.listAllCars(userService.getId(request.session.get("username").get)), Duration.Inf)
    pwForm.form.bindFromRequest.fold(
      errorForm => BadRequest(views.html.restricted(carForm.form, user, cars, nameForm.form, surnameForm.form, errorForm)),
      data => {
        userService.changePassword(id, data.newPW)
        Redirect(routes.Authentication.index())
      }
    )
  }
}