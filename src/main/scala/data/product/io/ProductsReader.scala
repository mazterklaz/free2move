package data.product.io

import config.AppConfig
import data.csv.{CsvReader, Result}
import data.product.model.Products
import org.apache.spark.sql.{Dataset, SparkSession}

class ProductsReader(sparkSession: SparkSession, appConfig: AppConfig) extends CsvReader[Dataset, Products] {

  import sparkSession.implicits._

  override def readCsv: Dataset[Result[Products]] = sparkSession
    .read
    .textFile(appConfig.productsPath)
    .map(Products.fromCsvLine)
}
