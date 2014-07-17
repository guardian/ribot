package ribot.elasticsearch

import org.elasticsearch.node.NodeBuilder
import com.sksamuel.elastic4s.ElasticClient
import com.sksamuel.elastic4s.ElasticDsl._


object Elasticsearch {

  val es = ElasticClient.local

  val client = es.client


  def stop() { es.close() }
}
