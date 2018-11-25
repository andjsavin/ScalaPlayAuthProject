package service

import com.google.inject.ImplementedBy
import models.User
import scala.concurrent.Future

@ImplementedBy(classOf[userServiceImpl])
trait userService {
  def addUser(user:User) : Future[String]
  def getUser(username: String): Option[User]
  def deleteUser(id : Int) : Future[Option[Int]]
  def listAllUsers : Future[Seq[User]]
  def getAdminCount(): Future[Int]
  def changeName(id: Int, name: String)
  def changeSurname(id: Int, surname: String)
  def changePassword(id: Int, pw: String)
  def auth(username: String, pw: String):Option[User]
  def getId(username: String): Int
}