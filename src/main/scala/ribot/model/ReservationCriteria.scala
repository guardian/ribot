package ribot.model

case class ReservationCriteria
(
  instanceType: InstanceType,
  az: String,
  networkClass: NetworkClass,
  numInstances: Int
) {
  def points = instanceType.sizeNormalistionFactor * numInstances
  def pointBreakdown = s"$numInstances * ${instanceType.sizeNormalistionFactor} = $points"

  def instanceClass = instanceType.instanceClass

  override def toString = s"$numInstances * ${instanceType.name} in $az ($networkClass)"
}

object ReservationCriteria {
  def unaggregate(criterias: List[ReservationCriteria]): List[ReservationCriteria] = {
    criterias.flatMap { c =>
      for (i <- 1 to c.numInstances) yield c.copy(numInstances = 1)
    }
  }

  def aggregate(unsorted: List[ReservationCriteria]): List[ReservationCriteria] = {
    unsorted.groupBy(c => (c.instanceType, c.az, c.networkClass))
      .values
      .toList
      .map { rclist =>
        rclist.head.copy(
          numInstances = rclist.map(_.numInstances).sum
        )
      }
      .sortBy(_.toString)
  }

  def superAggregate(unsorted: List[ReservationCriteria]): List[ReservationCriteria] = {
    unsorted.groupBy(c => c.instanceType)
      .values
      .toList
      .map { rclist =>
      rclist.head.copy(
        numInstances = rclist.map(_.numInstances).sum,
        az = "xxx"
      )
    }
    .sortBy(_.toString)
  }


}
