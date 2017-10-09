package net.shadowfacts.krypton.pipeline.selector

import net.shadowfacts.krypton.Metadata
import java.io.File

/**
 * @author shadowfacts
 */
interface PipelineSelector {

	fun select(metadata: Metadata, file: File): Boolean

}