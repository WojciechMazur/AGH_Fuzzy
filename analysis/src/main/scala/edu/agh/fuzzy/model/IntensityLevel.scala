package edu.agh.fuzzy.model

sealed trait IntensityLevel
object IntensityLevel{
  def fromInt(x: Int): IntensityLevel = x match {
    case 0 => Recovery
    case 1 => Low
    case 2 => Moderate
    case 3 => High
    case 4 => SubMaximal
    case 5 => Maximal
  }
case object Recovery extends IntensityLevel
case object Low extends IntensityLevel
case object Moderate extends IntensityLevel
case object High extends IntensityLevel
case object SubMaximal extends IntensityLevel
case object Maximal extends IntensityLevel
}

