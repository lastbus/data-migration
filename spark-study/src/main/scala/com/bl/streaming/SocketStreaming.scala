package com.bl.streaming

import org.apache.log4j.{Level, LogManager, Logger}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by MK33 on 2016/9/27.
  */
object SocketStreaming {

  def main(args: Array[String]) {

    LogManager.getRootLogger.setLevel(Level.WARN)
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    val sparkConf = new SparkConf().setMaster("local[2]").setAppName("socket study")
//    val sc = new SparkContext(sparkConf)
    val ssc = new StreamingContext(sparkConf, Seconds(10))
    ssc.checkpoint("/tmp/test")

    val lines = ssc.socketTextStream("10.201.129.78", 9999)
    val words = lines.flatMap(_.split(" "))
    val pairs = words.map(word => (word, 1))
    val accu = pairs.updateStateByKey[Int](updateFunction _)
    accu.print()
//    val wordCounts =words.countByValue()
//    wordCounts.print()
    ssc.start()
    ssc.awaitTermination()




  }

  def updateFunction(newValues: Seq[Int], runningCount: Option[Int]): Option[Int] = {
    val newCount = newValues.sum + runningCount.getOrElse(0)  // add the new values with the previous running count to get the new count
    Some(newCount)
    None
  }


}
