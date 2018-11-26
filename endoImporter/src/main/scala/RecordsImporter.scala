import better.files.File
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.joda.JodaModule
import com.moomeen.endo2java.EndomondoSession
import com.moomeen.endo2java.model.DetailedWorkout
import com.moomeen.endo2java.model.Sport.{RUNNING, TRAIL_RUNNING, TREADMILL_RUNNING}
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging
import org.joda.time.format.DateTimeFormat

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, ExecutionContextExecutor, Future}
import scala.util.{Failure, Success, Try}

object RecordsImporter extends App with LazyLogging {
  trait Unreadable
  case object UnreadableWorkoutDetails{
    def apply() = new DetailedWorkout() with Unreadable
  }

  implicit val executionContext: ExecutionContextExecutor = ExecutionContext.global
  val config = ConfigFactory.load()
  val username = config.getString("endomondo.username")
  val password = config.getString("endomondo.password")
  val outputDir = config.getString("importer.outputDir")

  val session = new EndomondoSession(username, password)

  val acceptedSports = Seq(
    RUNNING,
    TRAIL_RUNNING,
    TREADMILL_RUNNING
  )

  Try(session.login()).recover {
        case t:Throwable => System.err.println(s"Unabled to login with credentials $username @ $password")
        System.exit(-1)
  }

  val mapper = new ObjectMapper()
    .registerModule(new JodaModule())

  val workouts = session
    .getWorkoutsMultiThreaded.asScala
    .filter(workout => acceptedSports.contains(workout.getSport))
    .sortBy(_.getStartTime.getMillis)
    .map(_.getId)
    .reverse
    .map { id =>
      Future(session.getWorkout(id))
        .map(writeToFile)
    }.toList


  def writeToFile(detailedWorkout: DetailedWorkout): DetailedWorkout = {
    import better.files.Dsl.SymbolicOperations
    val dateFormat = DateTimeFormat.forPattern("yyyy-MM-dd_HHmm")
    val file: File = File(s"$outputDir/endo_${dateFormat.print(detailedWorkout.getStartTime)}.json")
    file < mapper.writeValueAsString(detailedWorkout)
    detailedWorkout
  }

  Future.foldLeft(workouts)(0){
    case (counter, _) => counter +1
  }.onComplete {
    case Success(counter) => println(s"Imported $counter workouts")
      System.exit(0)
    case Failure(exception) => System.err.println(s"Error accoured while importing workouts: ${exception.getMessage}")
      System.exit(-1)
  }



}
