import play.api.mvc.{Filter, RequestHeader, Result, WithFilters}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Global extends WithFilters(AccessLog) {

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