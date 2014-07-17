package lib

import java.lang.reflect.Field
import java.text.{DecimalFormat, NumberFormat}
import java.util.Locale

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.twirl.api.{Html, HtmlFormat}

// Normally I don't like these general purpose classes. But hey.
object ViewHelpers {

  def fmt(i: Int): String = NumberFormat.getInstance(Locale.UK).format(i)
  def fmt(l: Long): String = NumberFormat.getInstance(Locale.UK).format(l)

  def fmtDiff(l: Long): String = {
    val num = NumberFormat.getInstance(Locale.UK).format(l)
    if (l > 0) "+" + num else num
  }

  def fmt(f: Float): String = new DecimalFormat("#0.0").format(f)
  def fmt(f: Double): String = new DecimalFormat("#0.0").format(f)

  def fmtDiff(f: Double): String = {
    val num = fmt(f)
    if (f > 0.0) "+" + num else num
  }

  def fmtDisplay(date: DateTime): String = DateTimeFormat.forPattern("EEE d MMM H:mm").print(date)
  def fmtUrl(date: DateTime): String = DateTimeFormat.forPattern("YYYY-MM-dd").print(date)

  def percent(f: Float) =  new DecimalFormat("#0.0").format(f*100) + "%"
  def percent(d: Double) =  new DecimalFormat("#0.0").format(d*100) + "%"
  def percentDiff(f: Float) =  new DecimalFormat("+#0.0;-#0.0").format(f*100) + "%"
  def percentDiff(d: Double) =  new DecimalFormat("+#0.0;-#0.0").format(d*100) + "%"

  def dumpFieldValues(obj: AnyRef): List[(String, String)] = {
    obj.getClass.getDeclaredFields.toList.map { field:Field =>
      field.setAccessible(true)
      field.getName -> Option(field.get(obj)).map(_.toString).getOrElse("null")
    }
  }

}

