package net.shadowfacts.krypton.util.toposort

import com.google.common.collect.Sets
import java.util.*

/**
 * Adapted from https://github.com/MinecraftForge/MinecraftForge/blob/16bfd8cef1d12ee9ca0de1122addaf9916767ae9/src/main/java/net/minecraftforge/fml/common/toposort/TopologicalSort.java#L97
 *
 * @author shadowfacts
 */
object TopologicalSort {

	fun <T> sort(graph: Graph<T>): List<T> {
		val reversed = graph.reversed()
		val sorted = mutableListOf<T>()
		val visited = hashSetOf<T>()
		val expanded = hashSetOf<T>()

		reversed.forEach {
			explore(it, reversed, sorted, visited, expanded)
		}

		return sorted
	}

	private fun <T> explore(node: T, graph: Graph<T>, sorted: MutableList<T>, visited: MutableSet<T>, expanded: MutableSet<T>) {
		if (node in visited) {
			if (node in expanded) return

			val cycleList = Sets.difference(visited, expanded)
			throw RuntimeException("Unable to sort graph, likely cycle in $cycleList")
		}

		visited += node

		graph.edgesFrom(node).forEach { inbound ->
			explore(inbound, graph, sorted, visited, expanded)
		}

		sorted += node
		expanded += node
	}

	class Graph<T>: Iterable<T> {
		companion object {
			fun <T> from(all: Iterable<T>, dependencies: (T) -> Iterable<T>) = Graph<T>().apply {
				all.forEach { node ->
					addNode(node)
					dependencies(node).forEach { dep ->
						addEdge(node, dep)
					}
				}
			}
		}

		private val graph = mutableMapOf<T, SortedSet<T>>()
		private val ordered = mutableListOf<T>()

		fun addNode(node: T): Boolean {
			if (node in graph) return false

			ordered += node
			graph[node] = TreeSet(Comparator.comparingInt(ordered::indexOf))
			return true
		}

		fun addEdge(from: T, to: T) {
			if (from !in graph && to in graph) throw NoSuchElementException("Missing nodes from graph")

			graph[from]!! += to
		}

		fun removeEdge(from: T, to: T) {
			if (from !in graph && to in graph) throw NoSuchElementException("Missing nodes from graph")

			graph[from]!! -= to
		}

		fun edgeExists(from: T, to: T): Boolean {
			if (from !in graph && to in graph) throw NoSuchElementException("Missing nodes from graph")

			return to in graph[from]!!
		}

		fun edgesFrom(from: T): Set<T> {
			if (from !in graph) throw NoSuchElementException("Missing nodes from graph")

			return Collections.unmodifiableSortedSet(graph[from])
		}

		fun reversed(): Graph<T> {
			val reversed = Graph<T>()

			forEach {
				reversed.addNode(it)
			}

			forEach { from ->
				edgesFrom(from).forEach { to ->
					reversed.addEdge(to, from)
				}
			}

			return reversed
		}

		override fun iterator() = ordered.iterator()

		fun size() = graph.size

		fun isEmpty() = graph.isEmpty()

		override fun toString() = graph.toString()
	}

}