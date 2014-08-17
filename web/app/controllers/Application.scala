package controllers

import com.google.visualization.datasource.datatable.value.{DateTimeValue, ValueType}
import com.google.visualization.datasource.datatable.{TableRow, ColumnDescription, DataTable}
import com.google.visualization.datasource.render.JsonRenderer
import org.joda.time.{DateTimeZone, LocalTime, LocalDate, DateTime}
import play.api.mvc.{Action, Controller}
import ribot.billing.BillingData
import ribot.reservations.ReservationData
import lib.GoogleVisualizationHelpers._


import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  def index() = Action {
    Ok(views.html.welcome(BillingData()))
  }

  def showRegion(region: String) = Action {

    val billingData = BillingData().filterBy(_.region == region)

    val dt = new DataTable()
    dt.addColumn(new ColumnDescription("instanceType", ValueType.TEXT, "Instance Type"))
    dt.addColumn(new ColumnDescription("resv", ValueType.NUMBER, "Reserved Usage"))
    dt.addColumn(new ColumnDescription("ondemand", ValueType.NUMBER, "On Demand Usage"))



    val yesterdayAtEightPm = new LocalDate().minusDays(1).toDateTime(new LocalTime(20, 0), DateTimeZone.UTC)

    for (h <- billingData.forOneHour(yesterdayAtEightPm).pointsPerType) {
      dt.buildRow.value(h.instType).value(h.reservedPoints).value(h.ondemandPoints).add()
    }





    val s = dt.asJson

    Ok(views.html.regionSummary(region, BillingData().filterBy(_.region == region), s.toString))

  }

  def showClass(region: String, instanceClass: String) = Action {
    val billingData = BillingData().filterBy(_.region == region).filterBy(_.instanceType.instanceClass == instanceClass)

    val reservationData = ReservationData(region).forClass(instanceClass)

    val dt = new DataTable()
    dt.addColumn(new ColumnDescription("time", ValueType.DATETIME, "Date"))
    dt.addColumn(new ColumnDescription("resv", ValueType.NUMBER, "Reserved Usage"))
    dt.addColumn(new ColumnDescription("ondemand", ValueType.NUMBER, "On Demand Usage"))


    for (h <- billingData.pointsPerHour) {
      dt.buildRow.value(h.hour).value(h.reservedPoints).value(h.ondemandPoints).add()
    }

    val s = dt.asJson

    Ok(views.html.showClass(region, instanceClass, billingData, s.toString, reservationData))
  }

}
