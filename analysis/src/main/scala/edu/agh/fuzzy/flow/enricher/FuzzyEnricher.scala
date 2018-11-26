package edu.agh.fuzzy.flow.enricher

import edu.agh.fuzzy.model.Workout
import net.sourceforge.jFuzzyLogic.FIS

case class FuzzyEnricher(fis: FIS) extends EnrichmentFlow[Workout]{
  override lazy val enrichers = Seq(
    PointsEnricher(),
    IntensityEnricher(fis),
    CountersEnricher(),
    SummaryEnricher(fis)
  )
}
