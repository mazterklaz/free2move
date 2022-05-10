package data.item.model

import data.csv.{CsvEncoder, Result, Utils}
import data.error.DataError

import java.sql.Timestamp

case class Item(orderId: String,
                orderItemId: String,
                productId: String,
                sellerId: String,
                shippingLimittDate: Timestamp,
                price: Double,
                freightValue: Double)

object Item extends CsvEncoder[Item] {
  override val HEADER: String = "\"order_id\",\"order_item_id\",\"product_id\",\"seller_id\",\"shipping_limit_date\",\"price\",\"freight_value\""

  override def fromCsvLine(line: String): Result[Item] = if (line == HEADER) Result(DataError(line))
  else {
    line.split(",") match {
      case Array(orderId, orderItemId, productId, sellerId, shippingLimittDate, price, freightValue) ⇒
        Result(
          Item(
            orderId = orderId,
            orderItemId = orderItemId,
            productId = productId,
            sellerId = sellerId,
            shippingLimittDate = Utils.stringToTimestamp(shippingLimittDate),
            price = price.toDouble,
            freightValue = freightValue.toDouble
          )
        )
      case _ ⇒ Result.empty
    }
  }
}
