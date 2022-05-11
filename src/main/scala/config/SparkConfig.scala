package config

import org.apache.spark.SparkConf
import org.apache.spark.sql.SparkSession

object SparkConfig {

  val sparkConf = new SparkConf()
    .set("spark.sql.sources.partitionOverwriteMode", "dynamic")
    .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
//    .set("spark.scheduler.mode", "FAIR")

  def sparkSession(isLocal: Boolean): SparkSession = if(isLocal)
    SparkSession
      .builder()
      .appName("free2move")
      .master("local[*]")
      .config(sparkConf)
      .getOrCreate()
  else
    SparkSession
      .builder()
      .appName("free2move")
      .config(sparkConf)
      .getOrCreate()
}
