package data.item.io

import config.AppConfig
import data.csv.{CsvReader, Result}
import data.item.model.Item
import org.apache.spark.sql.{Dataset, SparkSession}

class ItemReader(sparkSession: SparkSession, appConfig: AppConfig) extends CsvReader[Dataset, Item] {

  import sparkSession.implicits._

  override def readCsv: Dataset[Result[Item]] = sparkSession
    .read
    .textFile(appConfig.itemsPath)
    .map(Item.fromCsvLine)
}
