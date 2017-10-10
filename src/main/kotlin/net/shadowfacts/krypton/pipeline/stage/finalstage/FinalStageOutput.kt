package net.shadowfacts.krypton.pipeline.stage.finalstage

import net.shadowfacts.krypton.Page

/**
 * @author shadowfacts
 */
class FinalStageOutput: FinalStage {

	override fun apply(page: Page, input: String) {
		page.output.apply {
			parentFile.mkdirs()
			writeText(input, Charsets.UTF_8)
		}
	}

}