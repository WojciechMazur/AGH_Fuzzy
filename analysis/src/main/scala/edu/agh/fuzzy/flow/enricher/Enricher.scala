package edu.agh.fuzzy.flow.enricher

trait Enricher[IN] {
  def name: String = getClass.getSimpleName
  def enrich(msg: EnrichedMessage[IN]): Any
}
