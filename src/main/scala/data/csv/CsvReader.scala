package data.csv

trait CsvReader[M[_], T] {
  def readCsv: M[Result[T]]
}