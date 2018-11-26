package edu.agh.fuzzy.flow.enricher

import edu.agh.fuzzy.model.{PointEnrichment, Workout}

case class PointsEnricher() extends Enricher[Workout] {

  override def name: String = PointsEnricher.name

  override def enrich(msg: EnrichedMessage[Workout]): Seq[PointEnrichment] = {
    val workout = msg.msg
    val timeDelta = workout.duration.toSeconds / workout.points.size
    val nPointToAverage = 10
    workout.points
      .sliding(nPointToAverage, 1)
      .filter(_.size >= 2)
      .map {
        seq =>
          val first = seq.head
          val second = seq.tail.head
          val distanceMeters = (second.distance - first.distance) * 1000
          val speed = distanceMeters / timeDelta
          val avgSpeed = (seq.last.distance - first.distance) * 1000 / (timeDelta * nPointToAverage)
          val hrs = seq.flatMap(_.heartRate)
          val avgHr = if (hrs.nonEmpty) Some(hrs.sum / hrs.size) else None
          val altitudeLevel = Math.tan((seq.last.altitude-first.altitude) / (seq.last.distance - first.distance))
          val x = PointEnrichment(first, speed.toFloat, avgSpeed.toFloat, altitudeLevel.toFloat ,avgHr)
          x
      }.toSeq
  }

}

object PointsEnricher{
  lazy val name = "points"
}
