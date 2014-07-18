package lib

import com.google.visualization.datasource.datatable.{DataTable, TableRow}
import com.google.visualization.datasource.datatable.value.DateTimeValue
import com.google.visualization.datasource.render.JsonRenderer
import org.joda.time.DateTime

object GoogleVisualizationHelpers {
  implicit class DataRowPimps(dr: TableRow) {
    def addCell(dt: DateTime) =
      dr.addCell(
        new DateTimeValue(
          dt.getYear,
          dt.getMonthOfYear - 1,
          dt.getDayOfMonth,
          dt.getHourOfDay,
          dt.getMinuteOfHour,
          dt.getSecondOfMinute,
          dt.getMillisOfSecond)
      )

    def addCell(bd: BigDecimal) = dr.addCell(bd.toDouble)

  }

  implicit class DataTablePimps(table: DataTable) {
    def buildRow = new TableRowBuilder(table)

    def asJson = JsonRenderer.renderDataTable(table, true, true, true).toString
  }

  class TableRowBuilder(dt: DataTable) {
    val row = new TableRow()
    
    def value(v: DateTime) = { row.addCell(v); this }
    def value(v: BigDecimal) = { row.addCell(v); this }
    def value(v: Double) = { row.addCell(v); this }
    def value(v: String) = { row.addCell(v); this }

    def add() = dt.addRow(row)
  }
}
