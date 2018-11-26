package edu.agh.fuzzy.flow.enricher

import edu.agh.fuzzy.Variable._
import edu.agh.fuzzy.model.{BodyStrainLevel, IntensityLevel, Workout}
import net.sourceforge.jFuzzyLogic.FIS

import scala.util.Try

case class IntensityEnricher(globalFis: FIS) extends Enricher[Workout] {

  override def name: String = IntensityEnricher.name

  override def enrich(msg: EnrichedMessage[Workout]): Any = {
    val seq = msg.enrichedPoints.toList
    val fis = globalFis.getFunctionBlock(IntensityEnricher.functinBlock)
    seq.map { point =>
      val slope = point.altitudeLevel
        .min(fis.getVariable(Tilt).getUniverseMax.toFloat)
        .max(fis.getVariable(Tilt).getUniverseMin.toFloat)

      fis.setVariable(HeartRate, point.avgHrPercentMax.getOrElse(fis.getVariable(HeartRate).getUniverseMin))

      fis.setVariable(Speed, point.avgSpeedKph
        .getOrElse(fis.getVariable(Speed).getUniverseMin)
        .min(fis.getVariable(Speed).getUniverseMax)
        .max(fis.getVariable(Speed).getUniverseMin)
      )

      fis.setVariable(Tilt, slope)

      Try(fis.evaluate())
        .recover {
          case e: Throwable => println(e.getMessage)
        }

      val intensity = fis.getVariable(Intensity).getValue.round
      val bodyStrain = fis.getVariable(BodyStrain).getValue.round

      point.copy(
        intensity = Some(IntensityLevel.fromInt(intensity.toInt)),
        bodyStrain = Some(BodyStrainLevel.fromInt(bodyStrain.toInt))
      )
    }
  }
}

object IntensityEnricher{
  lazy val name = "pointsWithIntensity"
  lazy val functinBlock = "workoutLocalIntensity"
}

