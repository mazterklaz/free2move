package data.statistics

import data.customer.model.Customer
import data.item.model.Item
import data.order.model.Order
import data.product.model.Products
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.Dataset

class Statistics(customerDS: Dataset[Customer],
                 orderDS: Dataset[Order],
                 itemDS: Dataset[Item],
                 productDS: Dataset[Products]
               ) {

  lazy val itemOrderRDD: RDD[(Item, Order)] = itemDS
    .rdd
    .keyBy(_.orderId)
    .leftOuterJoin(
      orderDS
        .rdd
        .keyBy(_.orderId)
    )
    .flatMap{case (_, (item, optOrder)) ⇒
      optOrder
        .map{order ⇒
          (item, order)
        }
    }

  lazy val itemOrderCustomer: RDD[(Item, Order, Customer)] = itemOrderRDD
    .keyBy(_._2.customerId)
    .leftOuterJoin(
      customerDS
        .rdd
        .keyBy(_.customerId)
    ).flatMap{case ((_, ((item, order), optCustomer))) ⇒
    optCustomer.map{ customer ⇒
      (item, order, customer)
    }
  }
}
