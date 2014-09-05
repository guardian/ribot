package ribot

import java.io.File

import ribot.billing.BillingCsvReader

object BillingCsvMain extends App  {

  val filename = "/Users/gtackley/billing/362307275615-aws-billing-detailed-line-items-with-resources-and-tags-2014-07.csv.zip"
  //val filename = "/Users/gtackley/billing/smaller.csv.zip"

  BillingCsvReader
    .parseZip(new File(filename))
    .filter(_.isEc2InstanceUsage)
    .filter(_.availabilityZone != null)


}
