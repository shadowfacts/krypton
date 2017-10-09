package net.shadowfacts.krypton

import java.io.File

/**
 * @author shadowfacts
 */
data class Metadata(
		val krypton: Krypton,
		val source: File
) {

	var output = krypton.config.getOutput(source)

}