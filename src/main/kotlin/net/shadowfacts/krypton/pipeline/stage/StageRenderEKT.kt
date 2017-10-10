package net.shadowfacts.krypton.pipeline.stage

import net.shadowfacts.ekt.EKT
import net.shadowfacts.krypton.Page
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

	override fun apply(page: Page, input: String): String {
		val env = Environment(page, input, cacheDir, includesDir, data)
		return EKT.render(env)
	}

	class Environment: EKT.TemplateEnvironment {
		override val rootName: String
		override val name: String
		override val cacheDir: File?
		override val data: Map<String, EKT.TypedValue>

		private val page: Page
		private val includesDir: File?

		override val template: String
		override val include: String
			get() = if (includesDir != null) File(includesDir, name).readText(Charsets.UTF_8) else throw RuntimeException("Unable to load include $name, not includes dir specified")

		constructor(page: Page, template: String, cacheDir: File?, includesDir: File?, data: Map<String, EKT.TypedValue>) {
			this.page = page
			this.template = template
			this.rootName = page.source.name
			this.name = rootName
			this.cacheDir = cacheDir
			this.includesDir = includesDir
			this.data = data.toMutableMap().apply {
				put("page", EKT.TypedValue(page, page::class.qualifiedName!!))
			}
		}

		constructor(name: String, parent: Environment, data: Map<String, EKT.TypedValue>?) {
			this.page = parent.page
			this.rootName = parent.rootName
			this.name = name
			this.cacheDir = parent.cacheDir
			this.includesDir = parent.includesDir
			this.data = (data ?: parent.data).toMutableMap().apply {
				put("page", EKT.TypedValue(page, page::class.qualifiedName!!))
			}

			this.template = include
		}

		override fun createChild(name: String, data: Map<String, EKT.TypedValue>?): EKT.TemplateEnvironment {
			return Environment(name, this, data)
		}
	}

}