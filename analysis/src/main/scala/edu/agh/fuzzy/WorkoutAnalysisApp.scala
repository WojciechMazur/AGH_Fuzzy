package edu.agh.fuzzy
import java.io.InputStream
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.file.scaladsl.Directory
import akka.stream.scaladsl.Framing.FramingException
import akka.stream.scaladsl.{FileIO, Flow, JsonFraming}
import akka.util.ByteString
import better.files.{File, Resource}
import com.typesafe.config.ConfigFactory
import edu.agh.fuzzy.flow.enricher.FuzzyEnricher
import edu.agh.fuzzy.model.Workout
import io.circe.parser._
import net.sourceforge.jFuzzyLogic.FIS
import net.sourceforge.jFuzzyLogic.plot.JFuzzyChart

import scala.concurrent.{ExecutionContext, Future}

object WorkoutAnalysisApp  extends App {
  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  implicit val executonContext: ExecutionContext = system.dispatcher

  val config = ConfigFactory.load()
  val inputFilesDir = File(config.getString("analysis.inputDir")).path
  val fis = FIS.load(resolveFCLFile, true)
  val fuzzyChart = JFuzzyChart.get()

  fuzzyChart.chart(fis)

  println(s"Working dir: $inputFilesDir")

  val workoutDecoder = Flow[ByteString]
    .via(JsonFraming.objectScanner(Int.MaxValue))
    .map(_.utf8String)
    .reduce(_ + _)
    .map(json => decode[Workout](json))
    .recover{
      case e:Error => system.log.warning("Failed to parse json. Reason: ", {e.getMessage})
        Left(e)
      case e:FramingException => system.log.warning("Json framing failed. Reasom: ", {e.getMessage})
        Left(e)
    }
    .collect{
      case Right(workout: Workout) => workout
    }

  val source = Directory.walk(inputFilesDir, Some(3))
    .filter(path => path.getFileName.toString.endsWith("json"))
    .map(path => FileIO.fromPath(path))
    .flatMapConcat(_.via(workoutDecoder))

  val workoutAnalysis = Flow[Workout]
    .via(FuzzyEnricher(fis).enrichmentFlow)
    .mapAsync(1) { workout =>
      initTable
      val neededRecovery = f"${workout.summary.neededRecovery.recommendedRecovery(workout.msg.duration).toUnit(TimeUnit.HOURS)}%3.2f hours"
      println("|" +
        f" ${workout.msg.startTime.toString.replace("T", " ")} |" +
        f" ${workout.msg.distance}%5.2f km |" +
        f" ${workout.msg.duration.toMinutes}%4d min |" +
        f" ${workout.msg.ascent}%4d m |" +
        f" ${workout.summary.profit}%10s |" +
        f" ${workout.summary.neededRecovery}%15s |" +
        f" $neededRecovery%18s " +
        "|"
      )
      Future.successful(workout)
    }
  lazy val initTable: Unit = {
    println(
      "|" +
        f" ${"Workout start"}%-19s |" +
        f" ${"Distance"}%-8s |" +
        f" ${"Duration"}%-8s |" +
        f" ${"Ascent"}%6s |" +
        f" ${"Profit"}%-10s |" +
        f" ${"Needed recovery"}%-8s |" +
        f" ${"Estimated recovery"}%-18s" +
        "|"
    )
    println("|" + "-" * 21 + "|" + "-" * 10 + "|" + "-" * 10 + "|" + "-" * 8 + "|" + "-" * 12 + "|" + "-" * 17 + "|" + "-" * 20 + "|")
  }
  source
    .via(workoutAnalysis)
    .runFold(0L)((acc, _) => acc + 1
    ).map(counter =>
    println(s"Analysed $counter messages")
  )
    .onComplete(_ => system.terminate())


  def resolveFCLFile: InputStream = {
    val filename = config.getString("analysis.fcl.file")
    val resourceStream: Option[InputStream] = Resource
      .from(this.getClass.getClassLoader)
      .asStream(filename)

    lazy val fileStream = File(filename).newInputStream

    resourceStream getOrElse fileStream
  }
}