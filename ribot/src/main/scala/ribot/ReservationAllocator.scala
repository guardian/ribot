package ribot

import ribot.model._

object ReservationAllocator {
  def makeItSo(unallocatedDesiredReservations: List[ReservationCriteria], groups: List[ReservationGroup]): List[ReservationGroup] = {

    //this is the instance class
    //println("This is the instance class " + unallocatedDesiredReservations.last.instanceType)


    unallocatedDesiredReservations.last.instanceType

    val unallocated = ReservationCriteria.unaggregate(unallocatedDesiredReservations)
      .sortBy(_.points).reverse

    val groupsWithPotentionalSparePoints =

   unallocated.foldLeft(groups) {

      case (acc, nextDesiredReservation) =>
        possiblyApply(acc, nextDesiredReservation)

    }


    groupsWithPotentionalSparePoints
      .map(groupWithPotentialSparePoint =>
          groupWithPotentialSparePoint.spendSpare(unallocatedDesiredReservations.last))

  }



    def possiblyApply(groups: List[ReservationGroup], r: ReservationCriteria): List[ReservationGroup] = {



      def possiblyApplyImpl(nextGroups: List[ReservationGroup]): List[ReservationGroup] = {

        //print("Size of Reservation Group")

        nextGroups match {
          case Nil => Nil
          case head :: rest if head.sparePoints >= r.points => head.spend(r) :: rest /*<-- why does this recurse?*/

          //case head :: rest if head.sparePoints > 0 && r == 0 => head.spendSpare(head.sparePoints, r) :: rest

          case head :: rest => head :: possiblyApplyImpl(rest)
        }

      }

      //groups(0).spendSpare(r) ::


      possiblyApplyImpl(groups)

    }

}
