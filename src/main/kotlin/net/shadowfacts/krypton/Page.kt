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

	val metadata: Metadata

	init {
		val parts = input.split("---")
		if (parts.size >= 2) {
			input = parts.drop(2).joinToString("---")

			val yaml = Yaml().load(parts[1])
			if (System.getProperty("krypton.metadata.debugFrontMatter").toBoolean()) {
				println("Front matter for $source: $yaml")
			}
			metadata = Metadata(this, (yaml as Map<String, Any>).toMutableMap())
		} else {
			metadata = Page.Metadata(this, mutableMapOf())
		}
	}

	class Metadata(private val page: Page, private val metadata: MutableMap<String, Any>) {

		operator fun get(name: String): Any? {
			return metadata[name] ?: page.krypton.getDefault(page.source, name)
		}

		operator fun set(name: String, value: Any) {
			metadata[name] = value
		}

		operator fun contains(name: String): Boolean {
			return name in metadata || page.krypton.hasDefault(page.source, name)
		}

	}

}