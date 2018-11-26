package edu.agh.fuzzy.flow.enricher

import edu.agh.fuzzy.Variable._
import edu.agh.fuzzy.model
import edu.agh.fuzzy.model.{Workout, WorkoutEnrichment}
import net.sourceforge.jFuzzyLogic.FIS

import scala.util.Try

case class SummaryEnricher(globalFis: FIS) extends Enricher[Workout] {
  override def name: String = SummaryEnricher.name

  override def enrich(msg: EnrichedMessage[Workout]): Any = {
    lazy val counters = msg.counters

    val fis = globalFis.getFunctionBlock(SummaryEnricher.functionBlock)
    fis.setVariable(Duration, msg.msg.duration.toMinutes
      .min(fis.getVariable(Duration).getUniverseMax.toLong)
      .max(fis.getVariable(Duration).getUniverseMin.toLong)
    )

    fis.setVariable(Distance, msg.msg.distance
      .min(fis.getVariable(Distance).getUniverseMax)
      .max(fis.getVariable(Distance).getUniverseMin)
    )

    fis.setVariable(Ascent,  msg.msg.ascent
    .min(fis.getVariable(Ascent).getUniverseMax.toInt)
    .max(fis.getVariable(Ascent).getUniverseMin.toInt)
    )

    fis.setVariable(HighStrain, msg.counters(CountersEnricher.highStrainsFraction)
      .min(fis.getVariable(HighStrain).getUniverseMax)
    )

    fis.setVariable(Accents, counters(CountersEnricher.accentsFraction))
    fis.setVariable(Recovery, counters(CountersEnricher.recoveriesFraction))

    Try(fis.evaluate())
      .recover {
        case e: Throwable => println(e.getMessage)
          e.printStackTrace()
          throw e
      }
    val profit = fis.getVariable(WorkoutProfit).getValue.round.toInt
    val recovery = fis.getVariable(NeededRecovery).getValue.round.toInt

    WorkoutEnrichment(
      profit = model.WorkoutProfit.fromInt(profit),
      neededRecovery = model.NeededRecovery.fromInt(recovery))
  }
}

object SummaryEnricher{
  lazy val name = "summary"
  lazy val functionBlock = "workoutSummary"
}
