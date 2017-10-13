package net.shadowfacts.krypton.config

import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

/**
 * @author shadowfacts
 */
class Configuration(internal val data: MutableMap<String, Any> = mutableMapOf()) {

	var source: File by config(::File)
	var output: File by config(::File)
	var plugins: File by config(::File, fallback = { File(source, "_plugins") })

	var port: Int by config(Integer::parseInt, fallback = { 8080 })

	constructor(init: Configuration.() -> Unit): this() {
		this.init()
	}

	fun getOutput(input: File): File {
		val relativeInput = source.toPath().relativize(input.toPath())
		return output.toPath().resolve(relativeInput).toFile()
	}

}

inline fun <reified T: Any> config(noinline factory: (String) -> T, noinline fallback: () -> T? = { null }, name: String? = null): ConfigDelegate<T> {
	return ConfigDelegate(T::class, factory, fallback = fallback, name = name)
}

class ConfigDelegate<T: Any>(
		private val type: KClass<T>,
		private val factory: (String) -> T,
		private val fallback: () -> T?,
		private val name: String?
) {

	private var initialized = false
	private var value: T? = null

	operator fun getValue(thisRef: Configuration, property: KProperty<*>): T {
		if (!initialized) {
			val name = name ?: property.name
			value = if (name in thisRef.data) {
				val value = thisRef.data.getValue(name)
				when {
					type.isInstance(value) -> value as T
					value is String -> factory(value)
					else -> throw RuntimeException("Unable to convert config value $value for $name from ${value::class} to $type")
				}
			} else {
				fallback() ?: throw RuntimeException("No config value or fallback provided for $name")
			}
			initialized = true
		}

		return value!!
	}

	operator fun setValue(thisRef: Configuration, property: KProperty<*>, value: T) {
		initialized = false
		val name = name ?: property.name
		thisRef.data[name] = value
	}

}
