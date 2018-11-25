package traits


import javax.inject.{Inject, Singleton}
import models.User
import play.api.db.slick.DatabaseConfigProvider
import slick.jdbc.JdbcProfile

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

@Singleton
class userTraitImpl @Inject()(dbConfigProvider: DatabaseConfigProvider) extends userTrait {

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

  override def add(user: User): Future[String] = {
    db.run(users += user).map(res => "User successfully added").recover {
      case ex : Exception => ex.getCause.getMessage
    }
  }

  override def delete(id: Int): Future[Option[Int]] = {
    val c = getAdminCount()
    val user = getById(id)
    var i: Int = 0
    var usr: Option[User] = None
    i = Await.result(c, Duration.Inf)
    usr = Await.result(user, Duration.Inf)
    if (i > 1)
      db.run(users.filter(_.id === id).delete)
    if (i <= 1 && usr.get.isAdmin != 1) {
      db.run(users.filter(_.id === id).delete)
    }
      Future(None)
  }

  override def get(username: String): Option[User] = {
    Await.result(db.run(users.filter(_.username === username).result.headOption), Duration.Inf)
  }

  override def getById(id: Int): Future[Option[User]] = {
    db.run(users.filter(_.id === id).result.headOption)
  }

  override def listAll: Future[Seq[User]] = {
    db.run(users.result)
  }

  override def getAdminCount(): Future[Int] = {
    db.run(users.filter(_.isAdmin === 1).length.result)
  }

  override def changeName(id: Int, name: String) = {
    val query = for (user <- users if user.id === id)
      yield user.name
    db.run(query.update(name)) map { _ > 0 }
  }

  override def changeSurname(id: Int, surname: String) = {
    val query = for (user <- users if user.id === id)
      yield user.surname
    db.run(query.update(surname)) map { _ > 0 }
  }

  override def changePassword(id: Int, pw: String) = {
    val query = for (user <- users if user.id === id)
      yield user.password
    db.run(query.update(pw)) map { _ > 0 }
  }

  override def auth(username: String, pw: String): Option[User] = {
    val u = Await.result(db.run(sql"select userID from user where username = $username and password=$pw"
      .as[Int].headOption), Duration.Inf)
    if (u.isDefined) {
        return Await.result(getById(u.get), Duration.Inf)
      }
    None
    }

  override def getId(username: String): Int = {
    val u = Await.result(db.run(sql"select userID from user where username = $username"
      .as[Int].headOption), Duration.Inf)
    if (u.isDefined) {
      return u.get
      }
    -1
    }
  }