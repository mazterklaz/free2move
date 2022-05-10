package main

import com.typesafe.scalalogging.LazyLogging
import config.{AppConfig, SparkConfig}
import data.customer.io.CustomerReader
import data.customer.model.Customer
import data.item.io.ItemReader
import data.item.model.Item
import data.order.io.OrderReader
import data.order.model.Order
import data.product.io.ProductsReader
import data.product.model.Products
import data.statistics.{CustomerStatistics, Statistics}
import org.apache.spark.sql.{Dataset, SparkSession}

object Free2MoveApp extends App with LazyLogging {

  implicit val appConfig: AppConfig = AppConfig()

  implicit val sparkSession: SparkSession = SparkConfig.sparkSession(true)

  import sparkSession.implicits._

  val customerDS: Dataset[Customer] = new CustomerReader(sparkSession, appConfig).readCsv.flatMap(_.result)

  val orderDS: Dataset[Order] = new OrderReader(sparkSession, appConfig).readCsv.flatMap(_.result).filter(_.isBetween(appConfig.startDate, appConfig.endDate))

  val itemDS: Dataset[Item] = new ItemReader(sparkSession, appConfig).readCsv.flatMap(_.result)

  val productDS: Dataset[Products] = new ProductsReader(sparkSession, appConfig).readCsv.flatMap(_.result)

  val statistics: Statistics = new Statistics(customerDS, orderDS, itemDS, productDS)

  val customerStatistics = new CustomerStatistics(statistics.itemOrderCustomer.toDS, appConfig)

  customerStatistics.writeAll()


}
