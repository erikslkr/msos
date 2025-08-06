package graph

data class Graph(
    val vertices: Set<Vertex>,
    val edges: Set<Edge>,
)

data class Vertex(val id: String)

data class Edge(val from: Vertex, val to: Vertex)
