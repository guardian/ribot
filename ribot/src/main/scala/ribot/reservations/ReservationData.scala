package ribot.reservations

import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.ec2.AmazonEC2Client
import ribot.AWS
import ribot.model.Reservation

import scala.collection.GenSeq

case class ReservationData(region: String, all: GenSeq[Reservation]) {

  def forClass(instanceClass: String) = copy(
    all = all.filter(_.criteria.instanceType.instanceClass == instanceClass)
  )

  def totalPoints = all.map(_.points).sum

}


object ReservationData {
  import scala.collection.convert.decorateAll._

  private def loadData(regionName: String): ReservationData = {
    val region = Region.getRegion(Regions.fromName(regionName))

    val ec2 = region.createClient(classOf[AmazonEC2Client], AWS.credentials, null)

    val all = ec2.describeReservedInstances()
      .getReservedInstances.asScala
      .flatMap(Reservation.fromAws)
      .toList

    ReservationData(regionName, all)
  }

  // in due course this should cache
  def apply(regionName: String): ReservationData = loadData(regionName)
}