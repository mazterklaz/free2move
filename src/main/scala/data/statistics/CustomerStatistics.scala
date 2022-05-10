package data.statistics

import config.AppConfig
import data.customer.model.Customer
import data.item.model.Item
import data.order.model.Order
import org.apache.spark.sql.functions._
import org.apache.spark.sql.{DataFrame, Dataset, SaveMode}

class CustomerStatistics(dataset: Dataset[(Item, Order, Customer)], appConfig: AppConfig) {

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


  def writeSpending(numPartitions: Int = 1): Unit = customerSpendingDF
    .coalesce(numPartitions)
    .write
    .partitionBy("purchaseDate")
    .mode(SaveMode.Overwrite)
    .csv(appConfig.customerSpendingStatisticsPath)

  def writeOrders(numPartitions: Int = 1): Unit = customerOrderDF
    .coalesce(numPartitions)
    .write
    .partitionBy("purchaseDate")
    .mode(SaveMode.Overwrite)
    .csv(appConfig.customerOrdersStatisticsPath)

  def writeAll(numPartitions: Int = 1): Unit = {
    new Thread {
      override def run(): Unit = {
        writeSpending(numPartitions)
      }
    }.start()

    new Thread {
      override def run(): Unit = {
        writeOrders(numPartitions)
      }
    }.start()
  }

}
