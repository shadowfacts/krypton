package net.shadowfacts.krypton

import java.io.File

/**
 * @author shadowfacts
 */
interface Configuration {

	val source: File
	val output: File
	val plugins: File

	fun getOutput(input: File): File {
		val relativeInput = source.toPath().relativize(input.toPath())
		return output.toPath().resolve(relativeInput).toFile()
	}

}

class DefaultConfiguration(
		override val source: File,
		override val output: File,
		override val plugins: File = File(source, "_plugins")
): Configuration