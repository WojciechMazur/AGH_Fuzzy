package edu.agh.fuzzy

import scala.language.implicitConversions

abstract class Variable(val name: String)
object Variable{
  implicit def variableToString(variable: Variable): String = variable.name

  case object HeartRate extends Variable("hr")
  case object Speed extends Variable("speed")
  case object Tilt extends Variable("slope")
  case object Intensity extends Variable("intensity")
  case object BodyStrain extends Variable("bodyStrain")

  case object Duration extends Variable("duration")
  case object Distance extends Variable("distance")
  case object Accents extends Variable("accentsAmount")
  case object Recovery extends Variable("recoveryAmount")
  case object Ascent extends Variable("ascent")
  case object WorkoutProfit extends Variable("workoutProfit")
  case object NeededRecovery extends Variable("neededRecovery")
  case object HighStrain extends Variable("highStrain")

}

