package net.shadowfacts.krypton

import fi.iki.elonen.NanoHTTPD
import net.shadowfacts.krypton.config.Configuration
import net.shadowfacts.krypton.pipeline.Pipeline
import net.shadowfacts.krypton.pipeline.PipelineBuilder
import net.shadowfacts.krypton.pipeline.selector.PipelineSelector
import net.shadowfacts.krypton.pipeline.stage.finalstage.FinalStageOutput
import net.shadowfacts.krypton.util.StaticServer
import org.yaml.snakeyaml.Yaml
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import java.nio.file.*
import kotlin.concurrent.thread

/**
 * @author shadowfacts
 */
class Krypton {

	lateinit var config: Configuration private set
	private var pipelines = listOf<Pipeline>()
	private val pipelinePriorities = mutableMapOf<Pipeline, Int>()
	private val echoPipeline = Pipeline(selector = object: PipelineSelector {
		override fun select(page: Page, file: File) = false
	}, final = FinalStageOutput())
	private val pages = mutableMapOf<File, Pair<Page, Pipeline>>()
	private val defaults = mutableMapOf<File, Map<String, Any>>()

	constructor(config: Configuration) {
		this.config = config

		loadPlugins()
	}

	constructor(init: Configuration.(Krypton) -> Unit) {
		config = Configuration()
		config.init(this)

		loadPlugins()
	}

	private fun loadPlugins() {
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
		val files = config.source.walkTopDown().filter(File::isFile)
		val (defaultsFiles, pageFiles) = files.partition { it.name == "_defaults.yml" }

		defaultsFiles.forEach {
			defaults[it.parentFile] = Yaml().load(it.readText(Charsets.UTF_8)) as Map<String, Any>
		}

		pageFiles.forEach {
			scan(it)
		}
		pageFiles.forEach {
			val (page, pipeline) = pages[it]!!
			generate(page, pipeline)
		}
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
			val key = try {
				watcher.take()
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
						else {
							val (page, pipeline) = scan(child)
							generate(page, pipeline)
						}
					}
					StandardWatchEventKinds.ENTRY_DELETE -> {
						val dest = config.getOutput(child)
						if (dest.isDirectory) dest.deleteRecursively()
						else dest.delete()
					}
					StandardWatchEventKinds.ENTRY_MODIFY -> {
						if (child.isFile) {
							val (page, pipeline) = pages[child] ?: throw RuntimeException("")
							generate(page, pipeline)
						}
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
		val server = StaticServer(config.output, port)

		thread {
			server.start(NanoHTTPD.SOCKET_READ_TIMEOUT, false)
			println("Krypton server started on port $port")
		}

		Runtime.getRuntime().addShutdownHook(Thread {
			server.stop()
		})

		watch()
	}

	private fun scan(file: File): Pair<Page, Pipeline> {
		val page = Page(this, file)
		val pipeline = getPipeline(page)
		pipeline.scan(page)
		val pair = page to pipeline
		pages[file] = pair
		return pair
	}

	private fun generate(page: Page, pipeline: Pipeline) {
		pipeline.generate(page)
	}

	private fun getPipeline(page: Page) = pipelines.firstOrNull {
		it.matches(page, page.source)
	} ?: echoPipeline

	fun hasDefault(source: File, name: String): Boolean {
		return source.parentFile in defaults && name in defaults.getValue(source.parentFile)
	}

	fun getDefault(source: File, name: String): Any? {
		return defaults[source.parentFile]?.get(name)
	}

	fun createPipeline(init: PipelineBuilder.() -> Unit) {
		val builder = PipelineBuilder()
		builder.init()
		addPipeline(builder.build(), builder.priority)
	}

	fun addPipeline(pipeline: Pipeline, priority: Int) {
		pipelinePriorities[pipeline] = priority
		pipelines = pipelinePriorities.keys.sortedByDescending(pipelinePriorities::getValue)
	}

}