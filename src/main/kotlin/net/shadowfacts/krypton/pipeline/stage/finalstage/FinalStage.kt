package net.shadowfacts.krypton.pipeline.stage.finalstage

import net.shadowfacts.krypton.Page

/**
 * @author shadowfacts
 */
interface FinalStage {

	fun apply(page: Page, input: String)

}