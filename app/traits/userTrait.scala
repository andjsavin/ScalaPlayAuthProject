package traits

import com.google.inject.ImplementedBy
import models.User
import scala.concurrent.Future

@ImplementedBy(classOf[userTraitImpl])
trait userTrait {

  def add(user:User) : Future[String]
  def get(username: String): Option[User]
  def delete(id : Int) : Future[Option[Int]]
  def listAll : Future[Seq[User]]
  def getAdminCount(): Future[Int]
  def changeName(id: Int, name: String)
  def changeSurname(id: Int, surname: String)
  def changePassword(id: Int, pw: String)
  def auth(username: String, pw: String): Option[User]
  def getById(id: Int): Future[Option[User]]
  def getId(username: String): Int
}