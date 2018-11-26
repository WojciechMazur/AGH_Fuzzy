package edu.agh.fuzzy.flow.enricher

import edu.agh.fuzzy.model.{PointEnrichment, WorkoutEnrichment}

case class EnrichedMessage[T](
                          msg: T,
                          enrichments: Map[String, Any]
                          ){
  lazy val enrichedPoints: Seq[PointEnrichment] = enrichments(EnrichedMessage.enrichedPoints).asInstanceOf[Seq[PointEnrichment]]
  lazy val intensityAggregatedPoints: Seq[PointEnrichment] = enrichments(EnrichedMessage.pointsWithIntensity).asInstanceOf[Seq[PointEnrichment]]
  lazy val counters: Map[String, Double] = enrichments(EnrichedMessage.counter).asInstanceOf[Map[String, Double]]
  lazy val summary: WorkoutEnrichment = enrichments(SummaryEnricher.name).asInstanceOf[WorkoutEnrichment]
}

object EnrichedMessage{
  lazy val enrichedPoints: String = PointsEnricher.name
  lazy val pointsWithIntensity: String = IntensityEnricher.name
  lazy val counter: String = CountersEnricher.name

  def empty[T](msg: T) = EnrichedMessage(msg, Map.empty)
}
