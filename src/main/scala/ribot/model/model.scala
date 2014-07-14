package ribot.model

import com.amazonaws.services.ec2.model.ReservedInstances
import org.joda.time.{Duration, DateTime}



object InstanceSizeNormalisationFactor {

  // this list taken from http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ri-modification-instancemove.html
  // TODO: micro is now 0.5 becuase of t2's!
  private val instanceSizeToFactor = Map(
    "small" -> 1,
    "medium" -> 2,
    "large" -> 4,
    "xlarge" -> 8,
    "2xlarge" -> 16,
    "4xlarge" -> 32,
    "8xlarge" -> 64
  ).withDefaultValue(0)

  def apply(instanceSize: String): Int = instanceSizeToFactor(instanceSize)


  def combosFor(totalFactor: Int): Set[Set[String]] = {
    if (totalFactor == 0) Set.empty
    else {
      // all the different ways that the number can be divided up
      // start with the biggest size factor that doesn't exceed total
      // and recurively add all the remaining
      val x = for ((sizeName, size) <- instanceSizeToFactor if size <= totalFactor) yield {
        println(s"totalFactor = $totalFactor; trying $sizeName ($size)")
        Set(sizeName)
      }

      println("x = " + x)


      Set.empty
    }
  }
}


sealed trait NetworkClass
case object Classic extends NetworkClass
case object VPC extends NetworkClass

case class ReservationCriteria
(
  instanceType: InstanceType,
  az: String,
  networkClass: NetworkClass
)

case class Reservation
(
  criteria: ReservationCriteria,
  numInstances: Long,
  endDate: DateTime
) {

  // you can merge reservations where the hour is the same, even if the seconds differ
  // i.e. where this roundedEndDate is the same
  def roundedEndDate = endDate.withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0)

  require(numInstances > 0)
}

object Reservation {
  def fromAws(r: ReservedInstances): List[Reservation] = {
    if (r.getState != "active") Nil
    else {
      val criteria = ReservationCriteria(
        InstanceType.fromString(r.getInstanceType),
        r.getAvailabilityZone,
        Classic
      )

      List(Reservation(
        criteria,
        r.getInstanceCount.toLong,
        new DateTime(r.getEnd)
      )
      )
    }
  }
}


