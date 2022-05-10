package data.statistics

import config.SparkConfig
import data.csv.Utils
import data.customer.model.Customer
import data.item.model.Item
import data.order.model.Order
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class StatisticsTest extends AnyFlatSpec {

  "Given 2 orders at different dates for one customer, it" should "find one repeated customer with 2 orders at different date" in {

    val sparkSession = SparkConfig.sparkSession(true)
    import sparkSession.implicits._

    val ts1 = Utils.stringToTimestamp("2022-01-01 00:00:00")
    val ts2 = Utils.stringToTimestamp("2022-01-02 00:00:00")


    val order1 = Order(
      orderId = "order1",
      customerId = "samy",
      orderStatus = "ok",
      orderPurchaseTimestamp = ts1,
      orderApprovedAt = Some(ts1),
      orderDeliveredCarrierDate = Some(ts1),
      orderDeliveredCustomerDate = Some(ts1),
      orderEstimatedDeliveryDate = ts1,
      purchaseDate = Utils.timestampToDate(ts1)
    )

    val order2 = Order(
      orderId = "order2",
      customerId = "samy",
      orderStatus = "ok",
      orderPurchaseTimestamp = ts2,
      orderApprovedAt = Some(ts2),
      orderDeliveredCarrierDate = Some(ts2),
      orderDeliveredCustomerDate = Some(ts2),
      orderEstimatedDeliveryDate = ts2,
      purchaseDate = Utils.timestampToDate(ts2)
    )

    val customer = Customer(
      customerId = "samy",
      customerUniqueId = "samyzarour",
      customerZipCodePrefix = "34000",
      customerCity = "Montpellier",
      customerState = "Occitanie"
    )

    val item1 = Item(
      order1.orderId,
      "1",
      "macbook",
      "apple",
      ts1,
      3000,
      1
    )

    val item2 = Item(
      order2.orderId,
      "2",
      "usbc",
      "apple",
      ts1,
      10,
      1
    )

    val statistics = new Statistics(
      sparkSession.createDataset(
        Seq(customer)
      ),
      sparkSession.createDataset(
        Seq(order1, order2)
      ),
      sparkSession.createDataset(
        Seq(item1, item2)
      ),
      sparkSession.emptyDataset
    )

    val customerStatistics = new CustomerStatistics(statistics.itemOrderCustomer.toDS, null)

    val nbRepeatedOrders = customerStatistics.nbCustomerRepeaterDF.select("repeated_orders").first().getAs[Int](0)

    nbRepeatedOrders shouldEqual 2
  }
}
