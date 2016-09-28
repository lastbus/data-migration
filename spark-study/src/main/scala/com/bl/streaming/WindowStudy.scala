package com.bl.streaming

import org.apache.log4j.{Level, LogManager, Logger}
import org.apache.spark.streaming.{Seconds, StreamingContext}

/**
  * Created by MK33 on 2016/9/27.
  */
object WindowStudy {

  def main(args: Array[String]) {

    LogManager.getRootLogger.setLevel(Level.WARN)
    Logger.getLogger("org").setLevel(Level.OFF)
    Logger.getLogger("akka").setLevel(Level.OFF)

    val ssc = new StreamingContext("local[2]", "window study", Seconds(5))
    ssc.checkpoint("/tmp/test")

    val lines = ssc.socketTextStream("localhost", 9999)
    val words = lines.flatMap(_.split(" "))
    val pairs = words.map((_, 1))
    val window = pairs.window(Seconds(15), Seconds(5))
    window.reduceByKey(_ + _).print()

    pairs.reduceByKeyAndWindow((a: Int, b: Int) => a + b, Seconds(15), Seconds(5)).print()

    pairs.reduceByKeyAndWindow((a: Int, b: Int) => a + b, (a: Int, b: Int) => a - b, Seconds(15), Seconds(5)).filter(_._2 > 0).print()


    ssc.start()
    ssc.awaitTermination()


  }

}
