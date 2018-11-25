package traits

import com.google.inject.ImplementedBy
import models.car
import scala.concurrent.Future

@ImplementedBy(classOf[carTraitImpl])
trait carTrait {

  def add(car: car) : Future[String]
  def delete(id: Int) : Future[Int]
  def deleteUserCars(id: Int) : Future[Int]
  def listAll(id: Int) : Future[Seq[car]]
}