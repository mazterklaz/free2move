package data.item.model

import data.csv.{CsvEncoder, Result, Utils}
import data.error.DataError

import java.sql.Timestamp

/**
 * "order_id","order_item_id","product_id","seller_id","shipping_limit_date","price","freight_value"
 * "00010242fe8c5a6d1ba2dd792cb16214",1,"4244733e06e7ecb4970a6e2683c13e61","48436dade18ac8b2bce089ec2a041202",2017-09-19 09:45:35,58.90,13.29
 */
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
