package ribot.billing

import java.io.File

import org.elasticsearch.action.index.IndexRequestBuilder
import org.elasticsearch.client.Client
import play.api.libs.json.Json
import ribot.elasticsearch.{Elasticsearch, UsageHistoryMapping}
import ribot.model.Classic

object UsageElasticseachInserter {


  def insert(rawData: => Stream[BillingCsvRow], client: Client) {
    println("Inserting!")

    for (block <- rawData.filter(_.isEc2InstanceUsage).filter(_.availabilityZone != null).grouped(100)) {
      val bulkInsert = client.prepareBulk()

      block
        .foreach { r =>
          // TODO: need to figure out VPS vs Classic
          val usage = r.asUsage(Classic)
          bulkInsert.add(
            new IndexRequestBuilder(client, UsageHistoryMapping.indexName)
              .setId(r.recordId)
              .setType(UsageHistoryMapping.typeName)
              .setSource(
                Json.obj(
                  "endDate" -> usage.endDate,
                  "instanceType" -> usage.instanceType.name,
                  "instanceClass" -> usage.instanceType.instanceClass,
                  "instanceSize" -> usage.instanceType.instanceSize,
                  "instanceScalingFactor" -> usage.instanceType.sizeNormalistionFactor,
                  "region" -> usage.region,
                  "product" -> usage.networkClass.platformName,
                  "quantity" -> usage.quantity,
                  "unblendedRate" -> usage.hourlyCost.toDouble,
                  "unblendedCost" -> r.unblendedCost,
                  "reserved" -> usage.wasReserved
                ).toString()
              )
          )
        }

      val result = bulkInsert.get()

      if (result.hasFailures) println(result.buildFailureMessage())

    }
  }

  def go() {
    val filename = "/Users/gtackley/billing/362307275615-aws-billing-detailed-line-items-with-resources-and-tags-2014-07.csv.zip"
    //val filename = "/Users/gtackley/billing/smaller.csv.zip"

    UsageHistoryMapping.doApply(Elasticsearch.client)

    UsageElasticseachInserter.insert(
      BillingCsvReader.parseZip(new File(filename)), Elasticsearch.client
    )
  }
}
