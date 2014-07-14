package ribot

import ribot.model.{Reservation, Usage}

object Ribot {
//  def calcIdealReservations(usages: List[Usage]): List[Reservation] = {
//    usages.filter(_.averageUsagePerDay > 0).map { u =>
//      Reservation(u.instanceType, u.az, u.networkClass, u.averageUsagePerDay)
//    }
//  }

  case class DiffResult
  (
    matched: List[Reservation],
    unused: List[Reservation]
  )

  def calcDiffs(idealReservations: List[Reservation], actualReservations: List[Reservation]): DiffResult = {
    // this is over simplistic: we need to account for differeing numbers of reservations too
    val matched = idealReservations intersect actualReservations

    val unused = actualReservations diff matched


    DiffResult(matched, unused)

  }

}
