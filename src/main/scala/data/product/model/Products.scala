package data.product.model

import data.csv.Utils.strToOptDouble
import data.csv.{CsvEncoder, Result}
import data.error.DataError

case class Products(product_id: String,
                    product_category_name: String,
                    product_name_lenght: Option[Double],
                    product_description_lenght: Option[Double],
                    product_photos_qty: Option[Double],
                    product_weight_g: Option[Double],
                    product_length_cm: Option[Double],
                    product_height_cm: Option[Double],
                    product_width_cm: Option[Double],
                    product_category_name_english: String
                   )

object Products extends CsvEncoder[Products] {

  override def fromCsvLine(line: String): Result[Products] = if (line == HEADER) Result(DataError(line))
  else {
    line.split(",") match {
      case Array(product_id, product_category_name, product_name_lenght, product_description_lenght, product_photos_qty, product_weight_g, product_length_cm, product_height_cm, product_width_cm, product_category_name_english) ⇒
        Result(

          Products(
            product_id,
            product_category_name,
            strToOptDouble(product_name_lenght),
            strToOptDouble(product_description_lenght),
            strToOptDouble(product_photos_qty),
            strToOptDouble(product_weight_g),
            strToOptDouble(product_length_cm),
            strToOptDouble(product_height_cm),
            strToOptDouble(product_width_cm),
            product_category_name_english

          )
        )
      case _ ⇒ Result.empty
    }
  }

  override val HEADER: String = "product_id,product_category_name,product_name_lenght,product_description_lenght,product_photos_qty,product_weight_g,product_length_cm,product_height_cm,product_width_cm,product_category_name_english"
}