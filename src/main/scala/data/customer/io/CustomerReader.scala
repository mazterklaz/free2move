package data.customer.io

import config.AppConfig
import data.csv.{CsvReader, Result}
import data.customer.model.Customer
import org.apache.spark.sql.{Dataset, SparkSession}

class CustomerReader(sparkSession: SparkSession, appConfig: AppConfig) extends CsvReader[Dataset, Customer]{

  import sparkSession.implicits._

  override def readCsv: Dataset[Result[Customer]] = sparkSession
    .read
    .textFile(appConfig.customerPath)
    .map(Customer.fromCsvLine)
}
