package data.order.io

import config.AppConfig
import data.csv.{CsvReader, Result}
import data.order.model.Order
import org.apache.spark.sql.{Dataset, SparkSession}

class OrderReader(sparkSession: SparkSession, appConfig: AppConfig) extends CsvReader[Dataset, Order]{
  import sparkSession.implicits._

  override def readCsv: Dataset[Result[Order]] = sparkSession
    .read
    .textFile(appConfig.ordersPath)
    .map(Order.fromCsvLine)
}
