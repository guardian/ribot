package ribot

import java.io.File

import org.joda.time.{DateTime, DateTimeZone}
import ribot.billing.BillingCsvReader
import ribot.model._

object RibotMain extends App {

  //val filename = "/Users/gtackley/billing/362307275615-aws-billing-detailed-line-items-with-resources-and-tags-2014-07.csv.zip"
  val filename = "/Users/gtackley/billing/smaller.csv.zip"

  //  val yesterday = new LocalDate().minusDays(1)
  //  val yesterdayAtEightPm = yesterday.toDateTime(new LocalTime(20, 0), DateTimeZone.UTC)
  val hardcodedDateTime = new DateTime(2014, 7, 13, 20, 0, DateTimeZone.UTC)

  val usagesByRegion = BillingCsvReader
    .parseZip(new File(filename))
    .filter(_.isEc2InstanceUsage)
    .filter(_.availabilityZone != null)
    .filter(_.usageStartDate == hardcodedDateTime)
    // TODO: need to figure out VPS vs Classic
    .map(_.asUsage(Classic))
    .groupBy(_.region)
    .map { case (region, regionUsages) => UsagesByRegion(region, regionUsages.toList) }

  for (regionUsages <- usagesByRegion if regionUsages.region == "eu-west-1") {
    println(s"*** region: ${regionUsages.region} ***")

    val reservations = ReservationsByRegion.forRegion(regionUsages.region)

    // now by instance class
    for (instanceClass <- InstanceType.classes if instanceClass == "c3") {
      val desiredReservations = regionUsages.reservationsRequiredFor(instanceClass)
      val actualReservations = reservations.forClass(instanceClass)

      if (desiredReservations.size + actualReservations.size > 0) {
        println("  Instance class: " + instanceClass)

        val groups = ReservationGroup.make(actualReservations)

        println("These are the reservations that exists, and we want to spend:")
        for (group <- groups)
          println("       " + group)

        //ReservationCriteria.aggregate(actualReservationCriteria).foreach(r => println("      " + r))
        println("Based on what we used in our sample hour, this is what we want to reserve:")

        val unallocated = ReservationCriteria.unaggregate(desiredReservations)
          .sortBy(_.points).reverse

        val application = ReservationApplication(unallocated, ReservationGroups(groups))

        println("At the start: " + application)

        val result = application.makeItSo

        println("\n\nAt the end: " + result)

      }

    }

  }



}


case class ReservationGroups(groups: List[ReservationGroup]) {
  override def toString = groups.mkString("\n    ")

  def possiblyApply(r: ReservationCriteria): ReservationGroups = {
    def possiblyApplyImpl(nextGroups: List[ReservationGroup]): List[ReservationGroup] =
      nextGroups match {
        case Nil => Nil
        case head :: rest if head.sparePoints >= r.points => head.spend(r) :: rest
        case head :: rest => head :: possiblyApplyImpl(rest)
      }

    this.copy(groups = possiblyApplyImpl(groups))
  }
}


case class ReservationApplication
(
  unallocatedDesiredReservations: List[ReservationCriteria],
  reservationGroups: ReservationGroups
)  {

  def makeItSo: ReservationGroups = {
    unallocatedDesiredReservations.foldLeft(reservationGroups) { case (currentGroups, nextDesiredReservation) =>
      currentGroups.possiblyApply(nextDesiredReservation)
    }
  }

  override def toString = {
    s"unallocated:\n  ${unallocatedDesiredReservations.mkString("\n  ")}\n" +
    s"groups:\n  $reservationGroups"

  }
}

