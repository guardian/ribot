package ribot

import ribot.model._

object ReservationAllocator {
  def makeItSo(unallocatedDesiredReservations: List[ReservationCriteria], groups: List[ReservationGroup]): List[ReservationGroup] = {
    val unallocated = ReservationCriteria.unaggregate(unallocatedDesiredReservations)
      .sortBy(_.points).reverse

    unallocated.foldLeft(groups) { case (acc, nextDesiredReservation) =>
      possiblyApply(acc, nextDesiredReservation)
    }
  }

  def possiblyApply(groups: List[ReservationGroup], r: ReservationCriteria): List[ReservationGroup] = {
    def possiblyApplyImpl(nextGroups: List[ReservationGroup]): List[ReservationGroup] = {
      nextGroups match {
        case Nil => Nil
        case head :: rest if head.sparePoints >= r.points => head.spend(r) :: rest
        case head :: rest => head :: possiblyApplyImpl(rest)
      }
    }

    possiblyApplyImpl(groups)
  }

}