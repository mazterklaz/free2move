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

  lazy val nbCustomerRepeaterDF: DataFrame = dataset
    .rdd
    .map { case (_, order, customer: Customer) ⇒ customer.customerId -> order }
    .groupBy(_._1)
    .map { case ((customerId, iterable)) ⇒
      customerId -> iterable.map(_._2.purchaseDate.toString).toSeq.distinct.size
    }
    .filter(_._2 > 1)
    .toDF("customerId", "repeated_orders")


  def writeSpending(): Unit = customerSpendingDF
    .repartition($"purchaseDate")
    .write
    .option("header", "true")
    .partitionBy("purchaseDate")
    .mode(SaveMode.Overwrite)
    .csv(appConfig.customerSpendingStatisticsPath)

  def writeOrders(): Unit = customerOrderDF
    .repartition($"purchaseDate")
    .write
    .option("header", "true")
    .partitionBy("purchaseDate")
    .mode(SaveMode.Overwrite)
    .csv(appConfig.customerOrdersStatisticsPath)

  def writeRepeaters() = nbCustomerRepeaterDF
    .coalesce(1)
    .orderBy($"repeated_orders".desc)
    .write
    .option("header", "true")
    .mode(SaveMode.Overwrite)
    .csv(appConfig.customerRepeatersStatisticsPath)

  def writeAll(): Unit = {

    new Thread {
      override def run(): Unit = {
        val t0 = Instant.now().getEpochSecond
        logger.info(s"Start writing spending statistics from ${appConfig.startDate} to ${appConfig.endDate}")
        writeSpending()
        logger.info(s"Write spending finish in ${Instant.now().getEpochSecond - t0} seconds")
      }
    }.start()

    new Thread {
      override def run(): Unit = {
        val t0 = Instant.now().getEpochSecond
        logger.info(s"Start writing orders statistics from ${appConfig.startDate} to ${appConfig.endDate}")
        writeOrders()
        logger.info(s"Write orders finish in ${Instant.now().getEpochSecond - t0} seconds")
      }
    }.start()

    new Thread {
      override def run(): Unit = {
        val t0 = Instant.now().getEpochSecond
        logger.info(s"Start writing repeaters statistics from ${appConfig.startDate} to ${appConfig.endDate}")
        writeRepeaters()
        logger.info(s"Write repeaters finish in ${Instant.now().getEpochSecond - t0} seconds")
      }
    }.start()
  }

}
