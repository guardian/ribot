import play.api.mvc.{Result, RequestHeader, Filter, WithFilters}
import play.api.{Logger, Application, GlobalSettings}
import ribot.elasticsearch.Elasticsearch

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

object Global extends WithFilters(AccessLog) {


  override def onStop(app: Application) {
    println("closing...")
    Elasticsearch.stop()
    println("closed")
  }
}


object AccessLog extends Filter {
  override def apply(next: RequestHeader => Future[Result])(request: RequestHeader): Future[Result] = {
    val result = next(request)
    result.map { r =>
      if (!request.path.startsWith("/assets"))
        play.Logger.info(request + "\n\t => " + r)

      r
    }
  }
}