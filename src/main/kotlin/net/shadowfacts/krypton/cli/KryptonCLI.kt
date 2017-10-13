package net.shadowfacts.krypton.cli

import net.shadowfacts.krypton.config.Configuration
import net.shadowfacts.krypton.Krypton

/**
 * @author shadowfacts
 */
object KryptonCLI {

	@JvmStatic
	fun main(args: Array<String>) {
		val (options, remainingArgs) = parseCLIArgs(args)
		val config = Configuration(options)
		val krypton = Krypton(config)

		if (remainingArgs.size != 1) throw RuntimeException("Expected 1 command, got ${remainingArgs.size}")

		when (remainingArgs[0]) {
			"generate", "build" -> krypton.generate()
			"watch" -> krypton.watch()
			"serve" -> krypton.serve(config.port)
		}
	}

	private fun parseCLIArgs(args: Array<String>): Pair<MutableMap<String, Any>, List<String>> {
		val options = mutableMapOf<String, Any>()
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

		return options to remainingArgs
	}

}