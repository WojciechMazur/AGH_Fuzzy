package edu.agh.fuzzy.model

sealed trait BodyStrainLevel
object BodyStrainLevel{
  def fromInt(x: Int): BodyStrainLevel = x match {
    case 0 => VeryLow
    case 1 => Low
    case 2 => Moderate
    case 3 => High
    case 4 => VeryHigh
    case 5 => Extreme
  }

  case object VeryLow   extends BodyStrainLevel
  case object Low       extends BodyStrainLevel
  case object Moderate  extends BodyStrainLevel
  case object High      extends BodyStrainLevel
  case object VeryHigh  extends BodyStrainLevel
  case object Extreme   extends BodyStrainLevel
}

