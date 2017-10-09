package net.shadowfacts.krypton.pipeline

import net.shadowfacts.krypton.pipeline.selector.PipelineSelector
import net.shadowfacts.krypton.pipeline.stage.finalstage.FinalStage
import net.shadowfacts.krypton.pipeline.stage.Stage
import net.shadowfacts.krypton.pipeline.stage.finalstage.FinalStageOutput
import net.shadowfacts.krypton.util.dependencies.Dependencies
import net.shadowfacts.krypton.util.toposort.TopologicalSort

/**
 * @author shadowfacts
 */
class PipelineBuilder {

	lateinit var selector: PipelineSelector
	private val stages = mutableListOf<Stage>()
	private val dependencies = mutableMapOf<Stage, MutableList<String>>()
	var final: FinalStage? = FinalStageOutput()

	fun addStage(stage: Stage, dependencies: Dependencies<Stage>) {
		stages += stage
		getDepList(stage) += dependencies.after
		dependencies.before.map(this::getStage).forEach {
			getDepList(it) += stage.id
		}
	}

	fun getStage(id: String) = stages.firstOrNull {
		it.id == id
	} ?: throw RuntimeException("No registered stage with id $id")

	fun build(): Pipeline {
		val sortedStages = sortStages()
		return Pipeline(selector, sortedStages, final)
	}

	private fun sortStages(): MutableList<Stage> {
		val graph = TopologicalSort.Graph.from(stages, {
			val strs = dependencies[it] ?: listOf<String>()
			strs.map(this::getStage)
		})
		val sorted = TopologicalSort.sort(graph)
		if (System.getProperty("krypton.pipeline.debugDependencies").toBoolean()) {
			println("Pipeline stage dependencies: $graph")
			println("Pipeline stages sorted: $sorted")
		}
		return sorted.toMutableList()
	}

	private fun getDepList(stage: Stage): MutableList<String> {
		return dependencies.getOrPut(stage, ::mutableListOf)
	}

}