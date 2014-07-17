package controllers

import com.sksamuel.elastic4s.ElasticDsl._
import play.api.mvc.{Action, Controller}
import ribot.elasticsearch.{Queries, Elasticsearch, UsageHistoryMapping}

import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  def index() = Action.async {

    val countFuture = Elasticsearch.es.execute {
      count from UsageHistoryMapping.indexName -> UsageHistoryMapping.typeName
    }

    val regionsFuture = Queries.findAllRegions

    for (count <- countFuture; regions <- regionsFuture) yield {
      Ok(views.html.welcome(count.getCount, regions))
    }

  }

  def showRegion(region: String) = Action.async {
    for (instanceClasses <- Queries.instanceClassesForRegion(region)) yield {
      Ok(views.html.regionSummary(region, instanceClasses))
    }

  }

  def showClass(region: String, instanceClass: String) = Action.async {
    for {
      instanceClasses <- Queries.instanceClassesForRegion(region)
      x <- Queries.usagePerHour(region, instanceClass)
    } yield {
      Ok(views.html.showClass(region, instanceClass, instanceClasses))
    }
  }
}
