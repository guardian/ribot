package controllers

import com.google.visualization.datasource.datatable.value.{DateTimeValue, ValueType}
import com.google.visualization.datasource.datatable.{TableRow, ColumnDescription, DataTable}
import com.google.visualization.datasource.render.JsonRenderer
import org.joda.time.DateTime
import play.api.mvc.{Action, Controller}
import ribot.billing.BillingData
import ribot.reservations.ReservationData

import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  def index() = Action {
    Ok(views.html.welcome(BillingData()))
  }

  def showRegion(region: String) = Action {
    Ok(views.html.regionSummary(region, BillingData().filterBy(_.region == region)))
  }

  def showClass(region: String, instanceClass: String) = Action {
    val billingData = BillingData().filterBy(_.region == region).filterBy(_.instanceType.instanceClass == instanceClass)

    val reservationData = ReservationData(region).forClass(instanceClass)

    val dt = new DataTable()
    dt.addColumn(new ColumnDescription("time", ValueType.DATETIME, "Date"))
    dt.addColumn(new ColumnDescription("resv", ValueType.NUMBER, "Reserved"))
    dt.addColumn(new ColumnDescription("ondemand", ValueType.NUMBER, "On Demand"))

    import lib.GoogleVisualizationHelpers._

    for (h <- billingData.pointsPerHour) {
      dt.buildRow.value(h.hour).value(h.reservedPoints).value(h.ondemandPoints).add()
    }

    val s = dt.asJson

    Ok(views.html.showClass(region, instanceClass, billingData, s.toString, reservationData))
  }

}
