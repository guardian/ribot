package ribot.model

import scala.concurrent.duration.Duration
import java.util.concurrent.TimeUnit
import org.joda.time.DateTime

case class InstanceType(instanceClass: String, instanceSize: String) {
  val name = s"$instanceClass.$instanceSize"
  def sizeNormalistionFactor = InstanceSizeNormalisationFactor(instanceSize)
}

object InstanceSizeNormalisationFactor {

  // this list taken from http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ri-modification-instancemove.html
  private val instanceSizeToFactor = Map(
    "small" -> 1,
    "medium" -> 2,
    "large" -> 4,
    "xlarge" -> 8,
    "2xlarge" -> 16,
    "4xlarge" -> 32,
    "8xlarge" -> 64
  )

  def apply(instanceSize: String): Option[Int] = instanceSizeToFactor.get(instanceSize)


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


case class Reservation
(
  instanceType: InstanceType,
  az: String,
  networkClass: NetworkClass,
  numInstances: Long,
  endDate: DateTime
) {

  // you can merge reservations where the hour is the same, even if the seconds differ
  // i.e. where this roundedEndDate is the same
  def roundedEndDate = endDate.withMinuteOfHour(0).withSecondOfMinute(0).withMillisOfSecond(0)

  require(numInstances > 0)
}


// A key question here: is this the total number of instance hours assigned to this class?
// Or does it represent an individual instance starting?
// Actually the latter isn't that interesting. What I think you're looking for is the
// *average* number of instances running over the last month.
// SO working concept: this represents the *total* instance hours for a type/az/class over the
// last month
case class Usage
(
  instanceType: InstanceType,
  az: String,
  networkClass: NetworkClass,
  durationHours: Int
) {
  def duration = Duration(durationHours, TimeUnit.HOURS)
  def durationDays = duration.toDays

  def averageUsagePerDay = durationDays / 30
}