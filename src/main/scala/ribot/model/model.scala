package ribot.model

import org.joda.time.{Duration, DateTime}



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



case class Usage
(
  instanceType: InstanceType,
  az: String,
  networkClass: NetworkClass,
  startDate: DateTime,
  endDate: DateTime,
  quantity: Int,

  // and these bits for info only
  wasReserved: Boolean,
  hourlyCost: BigDecimal

) {
  def durationHours = new Duration(startDate, endDate).getStandardHours
  require(durationHours == 1, s"durationHours was $durationHours, expected 1")

  def region = az dropRight 1
}