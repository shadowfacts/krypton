package net.shadowfacts.krypton.pipeline.selector

import net.shadowfacts.krypton.Metadata
import java.io.File

/**
 * @author shadowfacts
 */
class PipelineSelectorExtension(private val extension: String): PipelineSelector {

	override fun select(metadata: Metadata, file: File): Boolean = file.extension.equals(extension, ignoreCase = true)

}