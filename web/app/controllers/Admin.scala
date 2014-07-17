package controllers

import play.api.mvc._
import ribot.billing.UsageElasticseachInserter

object Admin extends Controller {

  def elasticsearch() = Action { r =>
    Ok(views.html.admin.elasticsearch(r.flash.get("message")))
  }

  def populateElasticsearch() = Action { r =>
    UsageElasticseachInserter.go()

    Redirect(routes.Admin.elasticsearch()).flashing("message" -> "looks like it worked, son")
  }
}
