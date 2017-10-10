package net.shadowfacts.krypton.pipeline.selector

import net.shadowfacts.krypton.Page
import java.io.File

/**
 * @author shadowfacts
 */
interface PipelineSelector {

	fun select(page: Page, file: File): Boolean

}