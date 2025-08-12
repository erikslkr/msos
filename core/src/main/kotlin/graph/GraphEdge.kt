package graph

/**
 * An undirected graph edge between two different vertices.
 * Undirected means that the edge has no direction, i.e. `GraphEdge(from, to) == GraphEdge(to, from)` is always `true`.
 *
 * @throws IllegalArgumentException If [from] and [to] are the same vertex
 */
class GraphEdge(val from: GraphVertex, val to: GraphVertex) {
    init {
        require(from != to) {
            "Edge must be between two different vertices"
        }
    }

    /**
     * Creates a [Set] from the incident vertices of the edge.
     * This set will always have exactly two elements (because [from] cannot equal [to])
     */
    fun toSet(): Set<GraphVertex> = setOf(from, to)

    override fun toString(): String = "{$from, $to}"

    override fun equals(other: Any?): Boolean {
        if (this === other) {
            return true
        }
        if (other !is GraphEdge) {
            return false
        }
        return (from == other.from && to == other.to) || (to == other.from && from == other.to)
    }

    override fun hashCode(): Int = from.hashCode() + to.hashCode()
}
