package edu.agh.fuzzy.model

import scala.concurrent.duration.Duration

sealed trait NeededRecovery{
  def recoveryFactor: Double
  def recommendedRecovery(duration: Duration): Duration = duration * recoveryFactor
}

object NeededRecovery {
  def fromInt(x: Int): NeededRecovery = x match {
    case 0 => NotNeeded
    case 1 => Moderate
    case 2 => Long
    case 3 => VeryLong
  }

  case object NotNeeded extends NeededRecovery{
    override def recoveryFactor: Double = 1
  }
  case object Moderate extends NeededRecovery{
    override def recoveryFactor: Double = 12
  }
  case object Long extends NeededRecovery{
    override def recoveryFactor: Double = 24
  }
  case object VeryLong extends NeededRecovery{
    override def recoveryFactor: Double = 48
  }
}


