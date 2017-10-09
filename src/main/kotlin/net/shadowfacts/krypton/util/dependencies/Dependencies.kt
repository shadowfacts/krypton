package net.shadowfacts.krypton.util.dependencies

/**
 * @author shadowfacts
 */
class Dependencies<T: Any> {

	val before = mutableListOf<String>()
	val after = mutableListOf<String>()

	constructor(init: Dependencies<T>.() -> Unit) {
		init()
	}

}