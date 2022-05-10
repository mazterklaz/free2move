package data.customer.model

import data.csv.{CsvEncoder, Result, Utils}
import data.error.{DataError, Message}

case class Customer(customerId: String,
                    customerUniqueId: String,
                    customerZipCodePrefix: String,
                    customerCity: String,
                    customerState: String
                   )

object Customer extends CsvEncoder[Customer] {

  override val HEADER = "\"customer_id\",\"customer_unique_id\",\"customer_zip_code_prefix\",\"customer_city\",\"customer_state\""

  override def fromCsvLine(line: String): Result[Customer] = if (line == HEADER) Result(DataError(line))

  else {
    line.split(",") match {
      case Array(customerId, customerUniqueId, customerZipCodePrefix, customerCity, customerState) if Customer.isValid(customerCity, customerUniqueId) ⇒
        Result(Customer(customerId, customerUniqueId, customerZipCodePrefix, customerCity, customerState))

      case Array(customerId, _, customerZipCodePrefix, customerCity, _) if Customer.isError(customerZipCodePrefix, customerCity) ⇒
        Result(Customer.getErrors(customerZipCodePrefix, customerCity, customerId))

      case _ ⇒ Result.empty
    }
  }


  def isValid(customerCity: String, customerUniqueId: String): Boolean = customerCity.nonEmpty && customerUniqueId.nonEmpty

  def isError(customerZipCodePrefix: String, customerCity: String): Boolean = customerZipCodePrefix.isEmpty || customerCity.isEmpty

  def getErrors(customerZipCodePrefix: String, customerCity: String, customerId: String): Seq[DataError] = {

    lazy val emptyMessage = Message("empty", customerId)

    (customerZipCodePrefix.isEmpty, customerCity.isEmpty) match {
      case (true, true) ⇒ Seq(
        DataError(CustomerColumns.ZIP_CODE, emptyMessage),
        DataError(CustomerColumns.CITY, emptyMessage))
      case (true, false) ⇒ Seq(DataError(CustomerColumns.ZIP_CODE, emptyMessage))
      case _ ⇒ Seq(DataError(CustomerColumns.CITY, emptyMessage))
    }
  }

  def apply(customerId: String, customerUniqueId: String, customerZipCodePrefix: String, customerCity: String, customerState: String): Customer = {

    new Customer(
      Utils.removeQuotes(customerId),
      Utils.removeQuotes(customerUniqueId),
      Utils.removeQuotes(customerZipCodePrefix),
      customerCity,
      customerState
    )
  }


}
