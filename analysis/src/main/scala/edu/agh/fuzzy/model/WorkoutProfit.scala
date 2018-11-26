package edu.agh.fuzzy.model

sealed trait WorkoutProfit

object WorkoutProfit{
  def fromInt(x:Int): WorkoutProfit = x match {
    case 0 => Recovery
    case 1 => Endurance
    case 2 => Speed
  }

  case object Recovery extends WorkoutProfit
  case object Endurance extends WorkoutProfit
  case object Speed extends WorkoutProfit
}


