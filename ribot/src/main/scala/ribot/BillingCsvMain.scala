package ribot

import java.io.File

import org.joda.time.{DateTime, DateTimeZone, LocalTime, LocalDate}
import ribot.billing.{UsageElasticseachInserter, BillingCsvReader}
import ribot.elasticsearch.{UsageHistoryMapping, Elasticsearch}
import ribot.model.{UsagesByRegion, Classic}

object BillingCsvMain extends App  {

  val filename = "/Users/gtackley/billing/362307275615-aws-billing-detailed-line-items-with-resources-and-tags-2014-07.csv.zip"
  //val filename = "/Users/gtackley/billing/smaller.csv.zip"

  val esClient = Elasticsearch.client

  try {
    UsageHistoryMapping.doApply(esClient)

    UsageElasticseachInserter.insert(
      BillingCsvReader.parseZip(new File(filename)), esClient
    )
  } finally {
    esClient.close()
  }
}
