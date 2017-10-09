package net.shadowfacts.krypton.cli

import net.shadowfacts.krypton.Krypton

/**
 * @author shadowfacts
 */
object KryptonCLI {

	@JvmStatic
	fun main(args: Array<String>) {
		val config = CLIConfiguration(args)
		val krypton = Krypton(config)

		if (config.remainingArgs.size != 1) throw RuntimeException("Expected 1 command, got ${config.remainingArgs.size}")

		when (config.remainingArgs[0]) {
			"generate", "build" -> krypton.generate()
			"watch" -> krypton.watch()
			"serve" -> krypton.serve(config.port)
		}
	}

}