import analysis.SemanticAnalyzer
import graph.Graph
import graph.GraphEdge
import graph.GraphVertex
import lexer.Lexer
import mso.modelCheck
import parser.Parser

fun main() {
    val input = "\\forall u \\forall v \\forall X ((X(u) \\land (\\forall y \\forall z ((X(y) \\land E(y,z)) \\implies X(z)))) \\implies X(v))"
    val lexer = Lexer(input)
    val tokenStream = lexer.tokenize()
    val parser = Parser(tokenStream)
    val (formula, formulaSpan) = parser.parseFormula()

    val vertices = setOf(GraphVertex("a"), GraphVertex("b"), GraphVertex("c"), GraphVertex("d"), GraphVertex("e"), GraphVertex("f"))
    val edges = setOf(
        GraphEdge(GraphVertex("a"), GraphVertex("b")),
        GraphEdge(GraphVertex("a"), GraphVertex("c")),
        GraphEdge(GraphVertex("a"), GraphVertex("d")),
        GraphEdge(GraphVertex("b"), GraphVertex("c")),
        GraphEdge(GraphVertex("b"), GraphVertex("d")),
        GraphEdge(GraphVertex("c"), GraphVertex("d")),
        GraphEdge(GraphVertex("e"), GraphVertex("f")),
    )
    val graph = Graph(vertices, edges)

    val semanticAnalyzer = SemanticAnalyzer()
    val isValid = semanticAnalyzer.analyzeFormula(formula, formulaSpan)

    fun printErrors() {
        val red = "\u001b[31m"
        val bold = "\u001b[1m"
        val reset = "\u001b[0m"
        for (error in semanticAnalyzer.errors) {
            println("${red}${bold}ERROR: ${reset}${red}${error.message}${reset}")
            println(input)
            println(red + " ".repeat(error.span.start) + "^".repeat(error.span.end - error.span.start) + reset)
            println()
        }
    }

    fun printWarnings() {
        val yellow = "\u001b[33m"
        val bold = "\u001b[1m"
        val reset = "\u001b[0m"
        for (warning in semanticAnalyzer.warnings) {
            println("${yellow}${bold}WARNING: ${reset}${yellow}${warning.message}${reset}")
            println(input)
            println(yellow + " ".repeat(warning.span.start) + "^".repeat(warning.span.end - warning.span.start) + reset)
            println()
        }
    }

    printErrors()
    printWarnings()

    if (isValid) {
        println(modelCheck(graph, formula))
    }
}
