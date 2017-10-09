package net.shadowfacts.krypton.pipeline.selector

import net.shadowfacts.krypton.Metadata
import java.io.File

/**
 * @author shadowfacts
 */
class PipelineSelectorExtension(private vararg val extensions: String): PipelineSelector {

	override fun select(metadata: Metadata, file: File): Boolean = extensions.contains(file.extension.toLowerCase())

}