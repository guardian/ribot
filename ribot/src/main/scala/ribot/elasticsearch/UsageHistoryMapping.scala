package ribot.elasticsearch

import org.elasticsearch.client.Client
import org.elasticsearch.common.settings.ImmutableSettings
import play.api.libs.json.{Json, JsValue}



/* Each entry in usage history represents the end of a one-hour server execution */
object UsageHistoryMapping extends IndexMapping {

  val indexName = "aws"
  val typeName = "usage"

  val desc = indexName -> typeName
  
  override val mapping =     
    Json.obj(
      typeName -> Json.obj(
        disableAll,
        defaultToNonAnalysedString,
        "properties" -> Json.obj(
          "endDate" -> date,

          "instanceType" -> nonAnalysedString,
          "instanceClass" -> nonAnalysedString,
          "instanceSize" -> nonAnalysedString,
          "instanceSizingFactor" -> double,

          "region" -> nonAnalysedString,
          "product" -> nonAnalysedString,

          "quantity" -> long,
          "unblendedRate" -> double,
          "unblendedCost" -> double,

          "reserved" -> boolean
        )
      )
    )

  def doApply(client: Client) {
    println(s"Initialising $indexName index...")

    println("checking index exists...")
    if (!client.admin().indices().prepareExists(indexName).get().isExists) {
      val indexSettings = ImmutableSettings.settingsBuilder()
        .put("index.auto_expand_replicas", "0-2")
        .put("index.number_of_shards", "3")
        .build()

      println(s"creating index $indexName")
        client.admin().indices().prepareCreate(indexName)
          .setSettings(indexSettings)
          .addMapping(typeName, json)
          .get()
    } else {
      println(s"reapplying mappings to $indexName")
        client.admin().indices().preparePutMapping(indexName)
          .setType(typeName)
          .setSource(json)
          .get()


    }

  }

}
