package net.shadowfacts.krypton.cli

import net.shadowfacts.krypton.Configuration
import java.io.File

/**
 * @author shadowfacts
 */
class CLIConfiguration: Configuration {

	override val source: File
	override val output: File
	override val plugins: File

	val port: Int

	val remainingArgs: List<String>

	constructor(args: Array<String>) {
		val options = mutableMapOf<String, String>()
		val remainingArgs = mutableListOf<String>()

		var i = 0
		while (i < args.size) {
			when {
				args[i].startsWith("--") -> {
					val option = args[i].toLowerCase()
					if (i == args.lastIndex) throw RuntimeException("Missing option value for $option")
					options[option.substring(2)] = options[args[i++]]!!
				}
				args[i].startsWith("-D") -> {
					val parts = args[i].substring(2).split("=")
					System.setProperty(parts[0], parts[1])
				}
				else -> remainingArgs += args[i]
			}
			i++
		}

		this.remainingArgs = remainingArgs

		this.source = File(options["source"])
		this.output = File(options["output"])
		this.plugins = if ("plugins" in options) File(options["plugins"]) else File(source, "_plugins")

		this.port = options["port"]?.toInt() ?: 8080
	}
}