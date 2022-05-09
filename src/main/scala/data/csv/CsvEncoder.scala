package data.csv

trait CsvEncoder[T]{
  def fromCsvLine(line: String): Result[T]
  val HEADER: String
}
