package edu.agh.fuzzy.flow.enricher

import edu.agh.fuzzy.model.BodyStrainLevel.{High, Low, VeryHigh, VeryLow}
import edu.agh.fuzzy.model.{IntensityLevel, Workout}

case class CountersEnricher() extends Enricher[Workout] {

  override lazy val name: String = CountersEnricher.name


  override def enrich(msg: EnrichedMessage[Workout]): Any = {
    val intensities = msg.intensityAggregatedPoints.map(_.intensity)

    val all = intensities.size
    val recovery = intensities.count {
      case Some(IntensityLevel.Recovery) => true
      case _ => false
    }

    val accentsCount = intensities.count {
      case Some(IntensityLevel.High) | Some(IntensityLevel.Maximal) => true
      case _ => false
    }

    val bodyStrains = msg.intensityAggregatedPoints
      .flatMap(_.bodyStrain)
      .groupBy(identity)
      .mapValues(_.size)

    val highBodyStrains = (bodyStrains.get(High) :: bodyStrains.get(VeryHigh) :: Nil).flatten.sum
    val highBodyStrainsFraction = highBodyStrains.toFloat / bodyStrains.values.sum
    val lowBodyStrains = (bodyStrains.get(Low) :: bodyStrains.get(VeryLow) :: Nil).flatten.sum
    val lowBodyStrainsFraction = lowBodyStrains.toFloat / bodyStrains.values.sum

    import CountersEnricher._
    val enrichments: Map[String, Double] = Map(
      recoveries -> recovery.toDouble,
      recoveriesFraction -> recovery.toFloat / all,
      accents -> accentsCount.toDouble,
      accentsFraction -> accentsCount.toFloat / all,
      highStrainsFraction -> highBodyStrainsFraction,
      lowStrainsFraction -> lowBodyStrainsFraction,
      allPoints -> all.toDouble
    )
//    println(enrichments)
    enrichments
  }


}

object CountersEnricher{
  lazy val name = "counters"
  lazy val recoveries = "recovery"
  lazy val recoveriesFraction = "recoveriesFraction"
  lazy val accents = "accents"
  lazy val accentsFraction = "accentsFraction"
  lazy val allPoints = "all"
  lazy val highStrainsFraction = "highStrainFraction"
  lazy val lowStrainsFraction = "lowStrainFraction"

}
