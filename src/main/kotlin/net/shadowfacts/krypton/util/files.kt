package net.shadowfacts.krypton.util

import java.io.File

/**
 * @author shadowfacts
 */
fun File.withExtension(ext: String): File {
	return File(parentFile, nameWithoutExtension + "." + ext)
}

fun File.getParentsUpTo(top: File): List<File> {
	return getParentsUpTo(top, mutableListOf())
}

private fun File.getParentsUpTo(top: File, parents: MutableList<File>): List<File> {
	parents += parentFile
	return if (parentFile == top) {
		parents
	} else {
		parentFile.getParentsUpTo(top, parents)
	}
}