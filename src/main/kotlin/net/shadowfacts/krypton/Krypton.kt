package net.shadowfacts.krypton

import fi.iki.elonen.NanoHTTPD
import net.shadowfacts.krypton.pipeline.Pipeline
import net.shadowfacts.krypton.pipeline.PipelineBuilder
import net.shadowfacts.krypton.pipeline.selector.PipelineSelector
import net.shadowfacts.krypton.pipeline.stage.finalstage.FinalStageOutput
import net.shadowfacts.krypton.util.StaticServer
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.*
import kotlin.concurrent.thread

/**
 * @author shadowfacts
 */
class Krypton(val config: Configuration) {

	private val pipelines = mutableListOf<Pipeline>()
	private val echoPipeline = Pipeline(selector = object: PipelineSelector {
		override fun select(metadata: Metadata, file: File) = false
	}, final = FinalStageOutput())

	init {
		if (config.plugins.exists() && config.plugins.isDirectory) {
			val cl = ClassLoader.getSystemClassLoader() as URLClassLoader
			val m = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
			m.isAccessible = true
			config.plugins.listFiles().forEach {
				m.invoke(cl, it.toURI().toURL())
			}
		}
	}

	fun generate() {
		config.source.walkTopDown().filter(File::isFile).forEach(this::generate)
	}

	fun watch() {
		generate()
		val watcher = FileSystems.getDefault().newWatchService()
		val keys = mutableMapOf<WatchKey, Path>()

		val register: (File) -> Unit = {
			it.toPath().apply {
				val key = register(watcher, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_DELETE, StandardWatchEventKinds.ENTRY_MODIFY)
				keys[key] = this
			}
		}
		val registerAll: (File) -> Unit = {
			it.walkTopDown().filter(File::isDirectory).forEach(register)
		}

		registerAll(config.source)

		while (true) {
			val key: WatchKey
			try {
				key = watcher.take()
			} catch (e: InterruptedException) {
				throw RuntimeException(e)
			}
			val dir = keys[key] ?: continue

			for (event in key.pollEvents()) {
				val kind = event.kind()

				if (kind == StandardWatchEventKinds.OVERFLOW) continue

				val ev = event as WatchEvent<Path>
				val name = ev.context()
				val child = dir.resolve(name).toFile()

				when (kind) {
					StandardWatchEventKinds.ENTRY_CREATE -> {
						if (child.isDirectory) registerAll(child)
					}
					StandardWatchEventKinds.ENTRY_DELETE -> {
						val dest = config.getOutput(child)
						if (dest.isDirectory) dest.deleteRecursively()
						else dest.delete()
					}
					StandardWatchEventKinds.ENTRY_MODIFY -> {
						if (child.isFile) generate(child)
					}
				}
			}

			val valid = key.reset()
			if (!valid) {
				keys -= key

				if (keys.isEmpty()) {
					break
				}
			}
		}
	}

	fun serve(port: Int = 8080) {
		thread {
			StaticServer(config.output, port).start(NanoHTTPD.SOCKET_READ_TIMEOUT, false)
		}
		watch()
	}

	private fun generate(file: File) {
		val metadata = Metadata(this, file)
		getPipeline(metadata, file).apply(metadata, file)
	}

	private fun getPipeline(metadata: Metadata, file: File) = pipelines.firstOrNull {
		it.matches(metadata, file)
	} ?: echoPipeline

	fun createPipeline(init: PipelineBuilder.() -> Unit) {
		val builder = PipelineBuilder()
		builder.init()
		pipelines += builder.build()
	}

}