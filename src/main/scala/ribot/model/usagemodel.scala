package ribot.model

import org.joda.time.{Duration, DateTime}


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

  def reservationCriteria = ReservationCriteria(instanceType, az, networkClass)

}

case class UsagesByRegion(region: String, usages: List[Usage]) {
  def reservationsRequired = usages.map(_.reservationCriteria)
}

case class UsagesByInstanceClass(instanceClass: String, usages: List[Usage])