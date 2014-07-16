import play.api.mvc.{Result, RequestHeader, Filter, WithFilters}
import play.api.{Logger, Application, GlobalSettings}

import scala.concurrent.ExecutionContext.Implicits.global

import scala.concurrent.Future

object Global extends WithFilters(AccessLog) {
  override def onStart(app: Application): Unit = {
    println("hello")
  }
}


object AccessLog extends Filter {
  override def apply(next: RequestHeader => Future[Result])(request: RequestHeader): Future[Result] = {
    val result = next(request)
    result.map { r => play.Logger.info(request + "\n\t => " + r); r }
  }
}