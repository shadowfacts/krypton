package net.shadowfacts.krypton.pipeline.stage

import net.shadowfacts.ekt.EKT
import net.shadowfacts.krypton.Metadata
import java.io.File

/**
 * @author shadowfacts
 */
class StageRenderEKT(
		private val cacheDir: File? = null,
		private val includesDir: File? = null,
		private val data: Map<String, EKT.TypedValue>
): Stage() {

	override val id = "ekt"

	constructor(cacheDir: File?, includesDir: File?, init: EKT.DataProvider.() -> Unit): this(cacheDir, includesDir, EKT.DataProvider.init(init))

	override fun apply(metadata: Metadata, input: String): String {
		val env = Environment(metadata, input, cacheDir, includesDir, data)
		return EKT.render(env)
	}

	class Environment: EKT.TemplateEnvironment {
		override val rootName: String
		override val name: String
		override val cacheDir: File?
		override val data: Map<String, EKT.TypedValue>

		private val metadata: Metadata
		private val includesDir: File?

		override val template: String
		override val include: String
			get() = if (includesDir != null) File(includesDir, name).readText(Charsets.UTF_8) else throw RuntimeException("Unable to load include $name, not includes dir specified")

		constructor(metadata: Metadata, template: String, cacheDir: File?, includesDir: File?, data: Map<String, EKT.TypedValue>) {
			this.metadata = metadata
			this.template = template
			this.rootName = metadata.source.name
			this.name = rootName
			this.cacheDir = cacheDir
			this.includesDir = includesDir
			this.data = data.toMutableMap().apply {
				put("metadata", EKT.TypedValue(metadata, metadata::class.qualifiedName!!))
			}
		}

		constructor(name: String, parent: Environment, data: Map<String, EKT.TypedValue>?) {
			this.metadata = parent.metadata
			this.rootName = parent.rootName
			this.name = name
			this.cacheDir = parent.cacheDir
			this.includesDir = parent.includesDir
			this.data = (data ?: parent.data).toMutableMap().apply {
				put("metadata", EKT.TypedValue(metadata, metadata::class.qualifiedName!!))
			}

			this.template = include
		}

		override fun createChild(name: String, data: Map<String, EKT.TypedValue>?): EKT.TemplateEnvironment {
			return Environment(name, this, data)
		}
	}

}