package edu.agh.fuzzy.flow.enricher

import akka.NotUsed
import akka.stream.scaladsl.Flow

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}
import scala.util.{Failure, Success, Try}

trait EnrichmentFlow[IN] {
  def enrichers: Seq[Enricher[IN]]

  implicit val excecutionContxt: ExecutionContextExecutor = ExecutionContext.global

  def enrichmentFlow: Flow[IN, EnrichedMessage[IN], NotUsed] = Flow[IN]
    .map { msg =>
        enrichers.foldLeft(EnrichedMessage.empty(msg)) {
          case (acc, enricher) =>
            val enrichmentResult = Try(enricher.enrich(acc))
            enrichmentResult match {
              case Success(result) =>
                val enrichment = enricher.name -> result
                acc.copy(enrichments = acc.enrichments + enrichment)
              case Failure(e) =>
                println(e.getMessage)
                e.printStackTrace()
                acc
            }
        }
    }
}
