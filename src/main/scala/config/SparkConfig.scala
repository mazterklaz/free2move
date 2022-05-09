package config

import org.apache.spark.sql.SparkSession

object SparkConfig {

  def sparkSession(isLocal: Boolean): SparkSession = if(isLocal)
    SparkSession
      .builder()
      .appName("free2move")
      .master("local[*]")
      .getOrCreate()
  else
    SparkSession
      .builder()
      .appName("free2move")
      .getOrCreate()
}
