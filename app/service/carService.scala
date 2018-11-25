package service

import com.google.inject.ImplementedBy
import models.car
import scala.concurrent.Future

@ImplementedBy(classOf[carServiceImpl])
trait carService {

  def addCar(car: car) : Future[String]
  def deleteCar(id: Int) : Future[Int]
  def deleteUserCars(id: Int): Future[Int]
  def listAllCars(id: Int) : Future[Seq[car]]

}