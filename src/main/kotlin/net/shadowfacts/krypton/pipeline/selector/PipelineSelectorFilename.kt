package net.shadowfacts.krypton.pipeline.selector

import net.shadowfacts.krypton.Page
import java.io.File

/**
 * @author shadowfacts
 */
class PipelineSelectorFilename(private vararg val names: String): PipelineSelector {

	override fun select(page: Page, file: File) = file.name in names

}