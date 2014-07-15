package ribot.model

// This represents a group of reserved instances that are completely mergable and splittable
// (ie. they have the same end date)
case class ReservationGroup(existingReservations: List[Reservation], proposedReservations: List[ReservationCriteria] = Nil) {
  val totalPoints = existingReservations.map(_.points).sum
  val spentPoints = proposedReservations.map(_.points).sum
  val sparePoints = totalPoints - spentPoints

  override def toString =
    s"TOTAL: $totalPoints ==> SPENT: $spentPoints, left: $sparePoints"


  def spend(proposed: ReservationCriteria): ReservationGroup = {
    this.copy(
      proposedReservations = proposed :: proposedReservations
    )
  }

}

object ReservationGroup {
  def make(all: List[Reservation]): List[ReservationGroup] =
    all.groupBy(_.roundedEndDate)
      .values
      .toList
      .map(ReservationGroup(_))
}
