package ribot

import com.amazonaws.auth.profile.ProfileCredentialsProvider
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.ec2.AmazonEC2Client
import com.amazonaws.services.ec2.model.{DescribeReservedInstancesListingsRequest, DescribeReservedInstancesListingsResult}
import ribot.model.Reservation

import scala.collection.convert.decorateAll._

object ReservationMain extends App {
  println("Getting reservations")

  val regions = List(Regions.US_WEST_1, Regions.EU_WEST_1, Regions.US_WEST_1) map Region.getRegion

  val creds = new ProfileCredentialsProvider("profile billing")

  for (region <- regions) {
    val ec2 = region.createClient(classOf[AmazonEC2Client], creds, null)

    println(s"** region ${region.getName} **")

    val rawResult = ec2.describeReservedInstances()

    val resGroups = rawResult.getReservedInstances.asScala
      .flatMap(Reservation.fromAws)
      .groupBy(_.roundedEndDate)

    for ((dt, res) <- resGroups) {
      println(s"  End date: ${dt}")

      for ((instanceClass, rrr) <- res.groupBy(_.criteria.instanceType.instanceClass).toList.sortBy(_._1)) {
        println(s"      class $instanceClass: ${rrr.map(r => r.criteria.instanceType.sizeNormalistionFactor * r.numInstances).sum}")
      }
    }

  }




}
