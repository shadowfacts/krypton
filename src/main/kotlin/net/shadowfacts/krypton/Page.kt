package net.shadowfacts.krypton

import org.yaml.snakeyaml.Yaml
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

	private val metadata = mutableMapOf<String, Any>()

	init {
		val parts = input.split("---")
		if (parts.size >= 2) {
			input = parts.drop(2).joinToString("---")

			val yaml = Yaml().load(parts[1])
			if (System.getProperty("krypton.metadata.debugFrontMatter").toBoolean()) {
				println("Front matter for $source: $yaml")
			}
			metadata.putAll(yaml as Map<String, Any>)
		}
	}

	fun getMetadata(name: String): Any? {
		return metadata[name] ?: krypton.getDefault(source, name)
	}

	fun setMetadata(name: String, value: Any) {
		metadata[name] = value
	}

}