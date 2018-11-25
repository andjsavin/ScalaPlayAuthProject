package service

import javax.inject.{Inject, Singleton}
import traits.carTrait
import models.car

import scala.concurrent.Future

@Singleton
class carServiceImpl @Inject()(carDAO: carTrait) extends carService {
  override def addCar(car: car): Future[String] = {
    carDAO.add(car)
  }

  override def deleteCar(id: Int): Future[Int] = {
    carDAO.delete(id)
  }

  override def deleteUserCars(id: Int): Future[Int] = {
    carDAO.deleteUserCars(id)
  }

  override def listAllCars(id: Int): Future[Seq[car]] = {
    carDAO.listAll(id)
  }
}