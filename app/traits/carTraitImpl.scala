package traits


import javax.inject.{Inject, Singleton}
import models.{car, User}
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

@Singleton
class carTraitImpl @Inject()(dbConfigProvider: DatabaseConfigProvider) extends carTrait {

  val dbConfig = dbConfigProvider.get[JdbcProfile]

  import dbConfig._
  import driver.api._

  class UserTable(tag:Tag)
    extends Table[User](tag, "user") {
    def id = column[Int]("userID", O.PrimaryKey,O.AutoInc)
    def username = column[String]("username")
    def password = column[String]("password")
    def name = column[String]("name")
    def surname = column[String]("surname")
    def isAdmin = column[Int]("isAdmin")

    override def * =
      (id, username, password, name, surname, isAdmin) <> (User.tupled, User.unapply)
  }

  implicit val users = TableQuery[UserTable]

  class CarTable(tag:Tag)
    extends Table[car](tag, "car") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def brand = column[String]("brand")
    def color = column[String]("color")
    def t = column[String]("type")
    def userID = column[Int]("userID")
    def user = foreignKey("fk_car_user", userID, users) (_.id)
    override def * =
      (id, brand, color, t, userID) <> (car.tupled, car.unapply)
  }

  implicit val cars = TableQuery[CarTable]

  override def add(car: car): Future[String] = {
    db.run(cars += car).map(res => "User successfully added").recover {
      case ex : Exception => ex.getCause.getMessage
    }
  }

  override def delete(id: Int): Future[Int] = {
    db.run(cars.filter(_.id === id).delete)
  }

  override def deleteUserCars(id: Int) : Future[Int] = {
    db.run(cars.filter(_.userID === id).delete)
  }

  override def listAll(id: Int): Future[Seq[car]] = {
    db.run(cars.filter(_.userID === id).result)
  }
}