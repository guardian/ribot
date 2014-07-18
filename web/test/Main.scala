import com.google.visualization.datasource.datatable.value.{DateTimeValue, ValueType}
import com.google.visualization.datasource.datatable.{TableRow, ColumnDescription, DataTable}
import com.google.visualization.datasource.render.JsonRenderer
import org.joda.time.DateTime

object Main extends App {
  println("hello")

  val dt = new DataTable()

  dt.addColumn(new ColumnDescription("helo", ValueType.DATETIME, "Date"))
  dt.addColumn(new ColumnDescription("val", ValueType.NUMBER, "Value"))

  val r = new TableRow()
  val date = new DateTime(1972, 7, 20, 11, 0)
  r.addCell(new DateTimeValue(date.getYear, date.getMonthOfYear, date.getDayOfMonth, date.getHourOfDay, date.getMinuteOfHour, 0, 0))
  r.addCell(7)
  dt.addRow(r)

  val s = JsonRenderer.renderDataTable(dt, true, true, true)
  println(s)

}
