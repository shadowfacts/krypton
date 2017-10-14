package net.shadowfacts.krypton.pipeline.selector

import net.shadowfacts.krypton.Page
import java.io.File

/**
 * @author shadowfacts
 */
class PipelineSelectorAnd(private vararg val selectors: PipelineSelector): PipelineSelector {

	override fun select(page: Page, file: File) = selectors.all { it.select(page, file) }

}