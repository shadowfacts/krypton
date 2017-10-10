package net.shadowfacts.krypton

import java.io.File

/**
 * @author shadowfacts
 */
data class Page(
		val krypton: Krypton,
		val source: File
) {

	var input = source.readText(Charsets.UTF_8)
	var output = krypton.config.getOutput(source)

}