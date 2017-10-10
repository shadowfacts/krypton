package net.shadowfacts.krypton.pipeline.stage

import net.shadowfacts.krypton.Page

/**
 * @author shadowfacts
 */
abstract class Stage {

	abstract val id: String

	abstract fun apply(page: Page, input: String): String

	override fun toString(): String = id

}