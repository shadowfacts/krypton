package net.shadowfacts.krypton.util

import fi.iki.elonen.NanoHTTPD
import java.io.File
import java.nio.file.Files

/**
 * @author shadowfacts
 */
class StaticServer(private val root: File, port: Int): NanoHTTPD(port) {

	override fun serve(session: IHTTPSession): Response {
		val file = File(root, session.uri)
		return if (file.exists()) {
			if (file.isFile) {
				newFixedFileResponse(file)
			} else {
				newFixedFileResponse(File(file, "index.html"))
			}
		} else {
			newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "404 Not Found\nThe file '$file' could not be found.")
		}
	}

	private fun newFixedFileResponse(file: File) =
			newFixedLengthResponse(Response.Status.OK, Files.probeContentType(file.toPath()), file.inputStream(), file.length())

}