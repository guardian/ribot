package ribot.elasticsearch

import com.sksamuel.elastic4s.ElasticDsl._
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogram
import org.elasticsearch.search.aggregations.bucket.terms.Terms
import org.elasticsearch.search.aggregations.bucket.terms.Terms.Order
import org.joda.time.DateTime

import scala.collection.convert.wrapAll._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Queries {


  def findAllRegions: Future[List[String]] = {
    Elasticsearch.es.execute {
      search in UsageHistoryMapping.desc size 0 aggs (
        agg terms "region" field "region" size 20
      )
    }.map { r =>
      r.getAggregations.get[Terms]("region").getBuckets.toList.map { b =>
        b.getKey
      }
    }
  }

  def instanceClassesForRegion(region: String): Future[List[String]] = {
   Elasticsearch.es.execute {
     search in UsageHistoryMapping.desc size 0 query term("region", region) aggs (
       agg terms "classes" field "instanceClass" size 30 order Order.term(true)
     )
   }.map { r =>
     r.getAggregations.get[Terms]("classes").getBuckets.toList.map(_.getKey)
   }
  }
  
  
  case class Usage(hour: DateTime, onDemandPoints: Long, reservedPoints: Long)
  
  def usagePerHour(region: String, instanceClass: String): Future[List[Usage]] = {
    Elasticsearch.es.execute {
      search in UsageHistoryMapping.desc size 0 filter {
        and(termFilter("region", region), termFilter("instanceClass", instanceClass))
      } aggs (
        agg datehistogram "dates" interval DateHistogram.Interval.HOUR field "endDate" aggs (
          agg terms "reserved" field "reserved" aggs(
            agg sum "points" field "instanceSizingFactor"
          )
        )
      )
    }.map { r =>
      println(r)
      val buckets = r.getAggregations.get[Terms]("reserved").getBuckets.toList.map(_.getKey)
      ???
    }

  }

}
