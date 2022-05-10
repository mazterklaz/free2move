package data.statistics

import com.typesafe.scalalogging.LazyLogging
import config.AppConfig
import data.customer.model.Customer
import data.item.model.Item
import data.order.model.Order
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode}

import java.time.Instant

class CustomerStatistics(dataset: Dataset[(Item, Order, Customer)], appConfig: AppConfig) extends LazyLogging {

  import dataset.sparkSession.implicits._

  dataset.cache()

  lazy val customerSpendingDF: DataFrame = dataset
    .toDF("item", "order", "customer")
    .select("customer.customerId", "item.price", "order.purchaseDate")
    .groupBy("customerId", "purchaseDate")
    .agg(sum("price").as("price"))
    .orderBy($"price".desc)

  lazy val customerOrderDF: DataFrame = dataset
    .toDF("item", "order", "customer")
    .select("customer.customerId", "order.orderId", "order.purchaseDate")
    .groupBy("customerId", "purchaseDate")
    .agg(count("orderId").as("nb_orders"))
    .orderBy($"nb_orders".desc)


  def writeSpending(): Unit = customerSpendingDF
    .repartition($"purchaseDate")
    .write
    .partitionBy("purchaseDate")
    .mode(SaveMode.Overwrite)
    .csv(appConfig.customerSpendingStatisticsPath)

  def writeOrders(): Unit = customerOrderDF
    .repartition($"purchaseDate")
    .write
    .partitionBy("purchaseDate")
    .mode(SaveMode.Overwrite)
    .csv(appConfig.customerOrdersStatisticsPath)

  def writeAll(): Unit = {

    new Thread {
      override def run(): Unit = {
        val t0 = Instant.now().getEpochSecond
        logger.info("Start writing spending statistics")
        writeSpending()
        logger.info(s"Write spending finish in ${Instant.now().getEpochSecond - t0} seconds")
      }
    }.start()

    new Thread {
      override def run(): Unit = {
        val t0 = Instant.now().getEpochSecond
        logger.info("Start writing orders statistics")
        writeOrders()
        logger.info(s"Write orders finish in ${Instant.now().getEpochSecond - t0} seconds")
      }
    }.start()
  }

}
