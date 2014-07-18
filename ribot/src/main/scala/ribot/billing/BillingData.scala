package ribot.billing

import java.io.File

import org.joda.time.{DateTimeZone, LocalTime, LocalDate, DateTime}
import ribot.model.{Classic, Usage}

import scala.collection.GenSeq


case class BillingData(raw: GenSeq[Usage], parent: Option[BillingData] = None) {
  lazy val regions = raw.map(_.region).distinct.toList.sorted
  lazy val instanceClasses = raw.map(_.instanceType.instanceClass).distinct.toList.sorted

  lazy val firstDate = raw.map(_.endDate).minBy(_.getMillis)
  lazy val lastDate = raw.map(_.endDate).maxBy(_.getMillis)

  lazy val aggregatedUsage = Usage.aggregate(raw)
    .sortBy(u => u.instanceType.sizeNormalistionFactor -> s" ${u.reservedString} ${u.az} ${u.networkClass}")

  case class HourlyUsagePoints(hour: DateTime, reservedPoints: BigDecimal, ondemandPoints: BigDecimal)

  lazy val pointsPerHour: List[HourlyUsagePoints] = raw
    .groupBy(_.endDate)
    .map { case (hour, usages) =>
      val reservedPoints = usages.filter(_.wasReserved).map(_.reservationCriteria.points).sum
      val ondemandPoints = usages.filterNot(_.wasReserved).map(_.reservationCriteria.points).sum
      HourlyUsagePoints(hour, reservedPoints, ondemandPoints)
    }
    .toList
    .sortBy(_.hour.getMillis)

  // in some cases (e.g. for nav etc) we and to be able to refer to the full set of data
  def global: BillingData = parent.map(_.global).getOrElse(this)

  def filterBy(f: Usage => Boolean) = copy(raw filter f, parent = Some(this))

  def forOneHourYesterdayEvening: BillingData = {
    val yesterdayAtEightPm = new LocalDate().minusDays(1).toDateTime(new LocalTime(20, 0), DateTimeZone.UTC)
    filterBy(_.endDate == yesterdayAtEightPm)
  }


}



object BillingData {

  private def loadData: BillingData = {
    val filename = "/Users/gtackley/billing/smaller.csv.zip"

    val rawData = BillingCsvReader
      .parseZip(new File(filename))
      .filter(_.isEc2InstanceUsage)
      .filter(_.availabilityZone != null)
      // TODO: need to figure out VPS vs Classic
      .map(_.asUsage(Classic))
      .toList

    BillingData(rawData)
  }

  // in the future, we should automatically refresh this data every now and again, to keep
  // up to date with when amazon pushes. So this will become a real method running off a cache
  // rather than just a lazy val
  lazy val get = loadData

  def apply() = get

}
