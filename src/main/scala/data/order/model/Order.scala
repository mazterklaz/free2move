package data.order.model

import data.csv.{CsvEncoder, Result, Utils}
import data.error.{DataError, Message}

import java.sql.{Date, Timestamp}

case class Order(orderId: String,
                 customerId: String,
                 orderStatus: String,
                 orderPurchaseTimestamp: Timestamp,
                 orderApprovedAt: Option[Timestamp],
                 orderDeliveredCarrierDate: Option[Timestamp],
                 orderDeliveredCustomerDate: Option[Timestamp],
                 orderEstimatedDeliveryDate: Timestamp,
                 purchaseDate: Date
                ) {
  def isBetween(startDate: String, endDate: String): Boolean = purchaseDate.toString >= startDate && endDate <= endDate
}

object Order extends CsvEncoder[Order] {

  def isValid(orderApprovedAt: String, orderStatus: String): Boolean = orderApprovedAt.nonEmpty || orderStatus != "canceled"

  override def fromCsvLine(line: String): Result[Order] = if (line == HEADER) Result(DataError(line))
  else {
    line.split(",") match {
      case Array(orderId, customerId, orderStatus, orderPurchaseTimestamp, orderApprovedAt, orderDeliveredCarrierDate, orderDeliveredCustomerDate, orderEstimatedDeliveryDate) if isValid(orderApprovedAt, orderStatus) ⇒

        val purchaseTimestamp = Utils.stringToTimestamp(orderPurchaseTimestamp)

        Result(
          Order(
            orderId = Utils.removeQuotes(orderId),
            customerId = Utils.removeQuotes(customerId),
            orderStatus = orderStatus,
            orderPurchaseTimestamp = purchaseTimestamp,
            orderApprovedAt = Utils.stringToOptTimestamp(orderApprovedAt),
            orderDeliveredCarrierDate = Utils.stringToOptTimestamp(orderDeliveredCarrierDate),
            orderDeliveredCustomerDate = Utils.stringToOptTimestamp(orderDeliveredCustomerDate),
            orderEstimatedDeliveryDate = Utils.stringToTimestamp(orderEstimatedDeliveryDate),
            purchaseDate = Utils.timestampToDate(purchaseTimestamp)
          ))
      case Array(orderId, _, orderStatus, _, orderApprovedAt, _, _, _) ⇒
        val errors = (orderApprovedAt.isEmpty, orderStatus == "canceled") match {
          case (true, true) ⇒ Seq(
            DataError(OrderColumns.APPROVED_AT, Message("empty", orderId)),
            DataError(OrderColumns.STATUS, Message("canceled", orderId))
          )
          case (true, false) ⇒ Seq(DataError(OrderColumns.APPROVED_AT, Message("empty", orderId)))
          case _ ⇒ Seq(DataError(OrderColumns.STATUS, Message("canceled", orderId)))
        }

        Result(errors)
      case _ ⇒ Result.empty
    }
  }

  override val HEADER: String = "\"order_id\",\"customer_id\",\"order_status\",\"order_purchase_timestamp\",\"order_approved_at\",\"order_delivered_carrier_date\",\"order_delivered_customer_date\",\"order_estimated_delivery_date\""
}
