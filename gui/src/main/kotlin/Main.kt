import graph.Graph
import graph.GraphEdge
import graph.GraphVertex

fun main() {
    val vertices = mutableSetOf(
        GraphVertex("a"),
        GraphVertex("b"),
        GraphVertex("c"),
        GraphVertex("d"),
        GraphVertex("e"),
        GraphVertex("f"),
    )
    val edges = mutableSetOf(
        GraphEdge(GraphVertex("a"), GraphVertex("b")),
        GraphEdge(GraphVertex("b"), GraphVertex("c")),
        GraphEdge(GraphVertex("c"), GraphVertex("d")),
        GraphEdge(GraphVertex("d"), GraphVertex("e")),
        GraphEdge(GraphVertex("e"), GraphVertex("f")),
        GraphEdge(GraphVertex("f"), GraphVertex("a")),
    )
    val graph = Graph(vertices, edges)
    println(graph)
}
