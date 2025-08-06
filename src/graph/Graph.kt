package graph

data class Graph(
    val vertices: Set<Vertex>,
    val edges: Set<Edge>,
) {
    val vertexIndices: Map<Vertex, Int> = vertices.withIndex().associate { (i, v) -> v to i }

    fun vertexSets(): Sequence<Set<Vertex>> = sequence {
        for (mask in 0 until (1L shl vertices.size)) {
            val set = mutableSetOf<Vertex>()
            for ((vertex, index) in vertexIndices) {
                if ((mask shr index) and 1L == 1L) {
                    set.add(vertex)
                }
            }
            yield(set)
        }
    }
}

data class Vertex(val id: String)

data class Edge(val from: Vertex, val to: Vertex) {
    fun toSet() = setOf(from, to)
}
