package net.shadowfacts.krypton.pipeline.selector

import net.shadowfacts.krypton.Page
import java.io.File

/**
 * @author shadowfacts
 */
class PipelineSelectorExtension(private vararg val extensions: String): PipelineSelector {

	override fun select(page: Page, file: File): Boolean = extensions.contains(file.extension.toLowerCase())

}