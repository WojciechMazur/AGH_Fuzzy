package edu.agh.fuzzy.model

import com.typesafe.config.ConfigFactory

case class PointEnrichment(
  point: Point,
  speed: Double,
  avgSpeed: Float,
  altitudeLevel: Float,
  avgHr: Option[Int] = None,
  intensity: Option[IntensityLevel] = None,
  bodyStrain: Option[BodyStrainLevel] = None
                          ) {
  val speedKph: Option[Double]    = validDouble(speed * 3.6)
  val avgSpeedKph: Option[Double] = validDouble(avgSpeed * 3.6)

  val hrPercentMax: Option[Double] = point.heartRate.map(_ / PointEnrichment.hrMax).flatMap(validDouble)
  val avgHrPercentMax: Option[Double] = avgHr.map(_ / PointEnrichment.hrMax).flatMap(validDouble)

  def validDouble: Double => Option[Double] = {
    case v if v.isNaN || v.isInfinity => None
    case v => Some(v)
    case _ => None
  }
}

object PointEnrichment{
  lazy val hrMax: Double = ConfigFactory.load().getDouble("analysis.personal.hrMax")
}

