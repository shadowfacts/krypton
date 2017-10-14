package net.shadowfacts.krypton.pipeline.selector

import net.shadowfacts.krypton.Page
import java.io.File

/**
 * @author shadowfacts
 */
class PipelineSelectorPrefix(private vararg val prefixes: String): PipelineSelector {

	override fun select(page: Page, file: File) = prefixes.any { file.name.startsWith(it) }

}