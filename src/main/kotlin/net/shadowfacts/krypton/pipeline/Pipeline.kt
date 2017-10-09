package net.shadowfacts.krypton.pipeline

import net.shadowfacts.krypton.Metadata
import net.shadowfacts.krypton.pipeline.selector.PipelineSelector
import net.shadowfacts.krypton.pipeline.stage.finalstage.FinalStage
import net.shadowfacts.krypton.pipeline.stage.Stage
import java.io.File

/**
 * @author shadowfacts
 */
class Pipeline(private val selector: PipelineSelector, private val stages: MutableList<Stage> = mutableListOf(), private val final: FinalStage? = null) {

	fun matches(metdata: Metadata, file: File) = selector.select(metdata, file)

	fun apply(metadata: Metadata, file: File) {
		var text = file.readText(Charsets.UTF_8)
		stages.forEach {
			text = it.apply(metadata, text)
		}
		final?.apply(metadata, text)
	}

}