package edu.agh.fuzzy.model

import io.circe.{Decoder, HCursor}

case class Point(
  distance:Double,
  altitude:Double,
  heartRate: Option[Int],
  cadence: Option[Int],
  geolocation: Option[GeoLocation]
)

object Point{
  implicit val decoder: Decoder[Point] = (c: HCursor) => for {
    distance <- c.downField("dist").as[Double]
    altitude <- c.downField("alt").as[Double]
    hr <- c.downField("hr").as[Option[Int]]
    cadence <- c.downField("cad").as[Option[Int]]
    lng <- c.downField("lng").as[Option[Double]]
    lat <- c.downField("lat").as[Option[Double]]
  } yield {
    val geoLocation = (lng, lat) match {
      case (Some(lngValue), Some(latValue)) => Some(GeoLocation(lngValue, latValue))
      case _ => None
    }
    Point(distance, altitude, hr, cadence, geoLocation)
  }
}
