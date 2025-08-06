import graph.Edge
import graph.Graph
import graph.Vertex
import lexer.Lexer
import mso.modelCheck
import parser.Parser

fun main() {
    val input = "\\forall u \\forall v \\forall X ((X(u) \\land (\\forall y \\forall z ((X(y) \\land E(y,z)) \\implies X(z)))) \\implies X(v))"
    val lexer = Lexer(input)
    val tokenStream = lexer.tokenize()
    val parser = Parser(tokenStream)
    val formula = parser.parseFormula()

    val vertices = setOf(Vertex("a"), Vertex("b"), Vertex("c"), Vertex("d"), Vertex("e"), Vertex("f"))
    val edges = setOf(
        Edge(Vertex("a"), Vertex("b")),
        Edge(Vertex("a"), Vertex("c")),
        Edge(Vertex("a"), Vertex("d")),
        Edge(Vertex("b"), Vertex("c")),
        Edge(Vertex("b"), Vertex("d")),
        Edge(Vertex("c"), Vertex("d")),
        Edge(Vertex("e"), Vertex("f")),
    )
    val graph = Graph(vertices, edges)

    println(modelCheck(graph, formula))
}
