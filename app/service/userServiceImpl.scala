package service

import javax.inject.{Inject, Singleton}
import traits.userTrait
import models.User

import scala.concurrent.Future

@Singleton
class userServiceImpl @Inject()(userDAO: userTrait) extends userService {
  override def addUser(user: User): Future[String] = {
    userDAO.add(user)
  }

  override def deleteUser(id: Int): Future[Option[Int]] = {
    userDAO.delete(id)
  }

  override def getUser(username: String): Option[User] = {
    userDAO.get(username)
  }
  override def listAllUsers: Future[Seq[User]] = {
    userDAO.listAll
  }

  override def getAdminCount(): Future[Int] = {
    userDAO.getAdminCount()
  }

  override def changeName(id: Int, name: String): Unit = {
    userDAO.changeName(id, name)
  }

  override def changeSurname(id: Int, surname: String): Unit = {
    userDAO.changeSurname(id, surname)
  }

  override def changePassword(id: Int, pw: String): Unit = {
    userDAO.changePassword(id, pw)
  }

  override def auth(username: String, pw: String):Option[User] = {
    userDAO.auth(username, pw)
  }

  override   def getId(username: String): Int = {
    userDAO.getId(username)
  }
}