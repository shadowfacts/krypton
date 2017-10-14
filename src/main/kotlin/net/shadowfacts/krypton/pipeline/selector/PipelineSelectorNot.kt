package net.shadowfacts.krypton.pipeline.selector

import net.shadowfacts.krypton.Page
import java.io.File

/**
 * @author shadowfacts
 */
class PipelineSelectorNot(private val selector: PipelineSelector): PipelineSelector {

	override fun select(page: Page, file: File) = !selector.select(page, file)

}