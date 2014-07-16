import org.joda.time.DateTime
import ribot.model._
import org.scalatest._
import ribot.RibotMain


class StuffTest extends FlatSpec with Matchers {
//  "ribot" should "calculate ideal reservations" in {
//    val usage = List(
//      Usage(InstanceType("m3.2xlarge"), az = "eu-west-1a", networkClass = Classic, durationHours = 1500)
//    )
//
//    val idealReservations = Ribot.calcIdealReservations(usage)
//
//    idealReservations shouldBe List(
//      Reservation(InstanceType("m3.2xlarge"), az = "eu-west-1a", networkClass = Classic, numInstances = 2)
//    )
//
//    // TODO: what happens if I run 30 m3.2xlarge for just one day?
//    //  hmm. I think I get one possible ununcessary reservation.
//  }

//  "reservation diff" should "identify unused actual reservations" in {
//    val idealReservations = List(
//      Reservation(InstanceType("m3.2xlarge"), az = "eu-west-1a", networkClass = Classic, numInstances = 2)
//    )
//
//    val actualReservations = List(
//      Reservation(InstanceType("m3.2xlarge"), az = "eu-west-1a", networkClass = Classic, numInstances = 2),
//      Reservation(InstanceType("m1.small"), az = "eu-west-1a", networkClass = Classic, numInstances = 2)
//    )
//
//    val diff = Ribot.calcDiffs(idealReservations, actualReservations)
//
//    diff.matched shouldBe List(
//      Reservation(InstanceType("m3.2xlarge"), az = "eu-west-1a", networkClass = Classic, numInstances = 2)
//    )
//
//    diff.unused shouldBe List(
//      Reservation(InstanceType("m1.small"), az = "eu-west-1a", networkClass = Classic, numInstances = 2)
//    )
//  }

  // TODO:
  //  what do our reservations look like? Is each individual reservation sepaate?
  // YES: they are in chunks that cannot be arbitarily split.
  // Not all can be split, see http://docs.aws.amazon.com/AWSEC2/latest/UserGuide/ri-modification-instancemove.html:
  // "To upgrade or upsize your reservation, you must have enough of the smaller instance types in the
  // same reservation to consolidate into larger instance types, and your reservation's overall
  // instance size footprint does not change."
  // So doing one per day is a bad idea because you'll never be able to upsize!!


  "Ribot" should "be able to report the possible instance sizes that a reservation set can be combined into" in {
    // 3 reservation dates: A and B are compatible for merging; c is not
    val dateA = new DateTime(2014, 2, 11, 11, 15, 22, 55)
    val dateB = new DateTime(2014, 2, 11, 11, 15, 44, 30)
    val dateC = new DateTime(2014, 2, 15, 11, 15, 22, 55)

    def resrv(az: String, numInstances: Int, endDate: DateTime) = Reservation(
      ReservationCriteria(InstanceType.fromString("m3.xlarge"), az = az, networkClass = Classic, numInstances = numInstances),
      endDate = endDate,
      reservationId = "id"
    )
    val sampleReservations = List(
      resrv(az = "eu-west-1a", numInstances = 2, dateA),
      resrv(az = "eu-west-1b", numInstances = 2, dateB),
      resrv(az = "eu-west-1c", numInstances = 2, dateC)
    )

    val grouped = sampleReservations
      .groupBy(r => (r.criteria.instanceType.instanceClass, r.roundedEndDate))

    for (reservations <- grouped.values) {
      println("Avilable for reorg: class " + reservations.head.criteria.instanceType.instanceClass)
      println("sum of scaling size = " + reservations.map(_.criteria.instanceType.sizeNormalistionFactor).sum)
    }
  }

  "Size normalizer" should "be able to build possible combos" in {
    InstanceSizeNormalisationFactor.combosFor(0) shouldBe Stream.empty
    InstanceSizeNormalisationFactor.combosFor(1) shouldBe Stream(List("small"))
    InstanceSizeNormalisationFactor.combosFor(2).toSet shouldBe Set(List("medium"), List("small", "small"))

    // the change of ordering between medium-small and small-medium isn't actually a new combo,
    // but at the moment I can't think of a way of eliminating
    InstanceSizeNormalisationFactor.combosFor(3).toSet shouldBe
      Set(List("medium", "small"), List("small", "medium"), List("small", "small", "small"))

    InstanceSizeNormalisationFactor.combosFor(16).foreach { l =>
      println(l)
    }
  }
}
