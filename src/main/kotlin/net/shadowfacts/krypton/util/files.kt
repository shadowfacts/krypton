package net.shadowfacts.krypton.util

import java.io.File

/**
 * @author shadowfacts
 */
fun File.withExtension(ext: String): File {
	return File(parentFile, nameWithoutExtension + "." + ext)
}