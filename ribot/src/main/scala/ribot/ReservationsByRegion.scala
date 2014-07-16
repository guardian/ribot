package ribot

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.{Regions, Region}
import com.amazonaws.services.ec2.AmazonEC2Client
import ribot.model.Reservation

import scala.collection.convert.decorateAll._

case class ReservationsByRegion
(
  region: String,
  all: List[Reservation]
) {
  def forClass(instanceClass: String) = all.filter(_.criteria.instanceType.instanceClass == instanceClass)
}

object ReservationsByRegion {

  def forRegion(regionName: String): ReservationsByRegion = {
    val region = Region.getRegion(Regions.fromName(regionName))
    
    val ec2 = region.createClient(classOf[AmazonEC2Client], AWS.credentials, null)

    val all = ec2.describeReservedInstances()
      .getReservedInstances.asScala
      .flatMap(Reservation.fromAws)
      .toList
    
    ReservationsByRegion(regionName, all)
  }
}
