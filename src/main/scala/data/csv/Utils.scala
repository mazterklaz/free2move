package data.csv

import java.sql.{Date, Timestamp}
import scala.util.Try

object Utils {

  def timestampToDate(timestamp: Timestamp): Date = new Date(timestamp.getTime)

  def removeQuotes(field: String): String = field.replaceAll("\"", "")

  def stringToTimestamp(str: String): Timestamp = Timestamp.valueOf(str)

  def stringToOptTimestamp(str: String): Option[Timestamp] = if(str.nonEmpty) Some(Timestamp.valueOf(str)) else None

  def stringToDate(str: String): Date = Date.valueOf(str)

  def stringToOptDate(str: String): Option[Date] = if(str.nonEmpty) Some(Date.valueOf(str)) else None

  def strToOptDouble(str: String): Option[Double] = Try(str.toDouble).toOption

}
