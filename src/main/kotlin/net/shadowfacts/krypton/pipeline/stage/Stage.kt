package net.shadowfacts.krypton.pipeline.stage

import net.shadowfacts.krypton.Metadata

/**
 * @author shadowfacts
 */
abstract class Stage {

	abstract val id: String

	abstract fun apply(metadata: Metadata, input: String): String

	override fun toString(): String = id

}