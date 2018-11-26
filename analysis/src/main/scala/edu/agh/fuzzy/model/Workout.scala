package edu.agh.fuzzy.model

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneId}
import java.util.concurrent.TimeUnit

import io.circe.{Decoder, HCursor}

import scala.concurrent.duration.FiniteDuration

case class Workout(id: Long,
                   startTime: LocalDateTime,
                   points: Seq[Point],
                   duration: FiniteDuration,
                   distance: Double,
                   altitudeMin:Double,
                   altitudeMax:Double,
                   descent: Int,
                   ascent: Int,
                   speedAvg: Double,
                   speedMax: Double,
                   heartRateAvg: Option[Int],
                   heartRateMax: Option[Int],
                   cadenceAvg: Option[Int],
                   calories: Double,
                   burgersBurned: Double
                  ) {
}

object Workout {
  val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("y-M-d H:m:s z")
  implicit def decoder: Decoder[Workout] = (c: HCursor) => for {
    id <- c.downField("id").as[Long]
    startTime <- c.downField("start_time").as[Long]
      .map(Instant.ofEpochMilli)
      .map(_.atZone(ZoneId.systemDefault()).toLocalDateTime)
    points <- c.downField("points").as[Seq[Point]]
    duration <- c.downField("duration").as[Long]
      .map(FiniteDuration(_, TimeUnit.MILLISECONDS))
    distance <- c.downField("distance").as[Double]
    altitudeMin <- c.downField("altitude_min").as[Double]
    altitudeMax <- c.downField("altitude_max").as[Double]
    descent <- c.downField("descent").as[Int]
    ascent <- c.downField("ascent").as[Int]
    speedAvg <- c.downField("speed_avg").as[Double]
    speedMax <- c.downField("speed_max").as[Double]
    hrAvg <- c.downField("heart_rate_avg").as[Option[Int]]
    hrMax <- c.downField("heart_rate_min").as[Option[Int]]
    cadenceAvg <- c.downField("cadence_avg").as[Option[Int]]
    calories <- c.downField("calories").as[Double]
    burgersBurned <- c.downField("burgers_burned").as[Double]
  } yield Workout(id, startTime, points, duration, distance, altitudeMin, altitudeMax, descent, ascent, speedAvg, speedMax, hrAvg, hrMax, cadenceAvg, calories, burgersBurned)
}
