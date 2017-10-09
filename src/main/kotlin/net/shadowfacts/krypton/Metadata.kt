package net.shadowfacts.krypton

import java.io.File

/**
 * @author shadowfacts
 */
data class Metadata(
		val krypton: Krypton,
		val source: File
) {

	val output: File
		get() = krypton.config.getOutput(source)

}