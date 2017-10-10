package net.shadowfacts.krypton.pipeline

import net.shadowfacts.krypton.Page
import net.shadowfacts.krypton.pipeline.selector.PipelineSelector
import net.shadowfacts.krypton.pipeline.stage.finalstage.FinalStage
import net.shadowfacts.krypton.pipeline.stage.Stage
import java.io.File

/**
 * @author shadowfacts
 */
class Pipeline(private val selector: PipelineSelector, private val stages: MutableList<Stage> = mutableListOf(), private val final: FinalStage? = null) {

	fun matches(page: Page, file: File) = selector.select(page, file)

	fun scan(page: Page) {
		stages.forEach {
			it.scan(page)
		}
	}

	fun generate(page: Page) {
		stages.forEach {
			page.input = it.apply(page, page.input)
		}
		final?.apply(page, page.input)
	}

}