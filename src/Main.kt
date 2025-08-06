import lexer.Lexer
import parser.Parser

fun main() {
    val input = "\\exists x \\exists y (E(x,y) \\land \\exists z (E(z,x) \\land E(z,y)))"
    val lexer = Lexer(input)
    val tokenStream = lexer.tokenize()
    val parser = Parser(tokenStream)
    val formula = parser.parseFormula()
    println(formula)
}
