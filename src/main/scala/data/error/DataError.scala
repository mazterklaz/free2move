package data.error

case class DataError(columnName: String,
                     message: Message)

object DataError{
  def apply(header: String): DataError = DataError("", Message(header))
}