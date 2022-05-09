package main

import config.{AppConfig, SparkConfig}
import data.csv.Result
import data.customer.io.CustomerReader
import data.customer.model.Customer
import data.item.io.ItemReader
import data.item.model.Item
import data.order.io.OrderReader
import data.order.model.Order
import data.product.io.ProductsReader
import data.product.model.Products
import org.apache.spark.sql.{Dataset, SparkSession}

object Free2MoveApp extends App {

  implicit val appConfig: AppConfig = AppConfig()

  implicit val sparkSession: SparkSession = SparkConfig.sparkSession(true)

  val customerDS: Dataset[Result[Customer]] = new CustomerReader(sparkSession, appConfig).readCsv
  val orderDS: Dataset[Result[Order]] = new OrderReader(sparkSession, appConfig).readCsv
  val itemDS: Dataset[Result[Item]] = new ItemReader(sparkSession, appConfig).readCsv
  val productDS: Dataset[Result[Products]] = new ProductsReader(sparkSession, appConfig).readCsv

}
