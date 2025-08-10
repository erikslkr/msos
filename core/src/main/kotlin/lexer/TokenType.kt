package lexer

sealed class TokenType(private val repr: String) {
    // MSO operators
    object Exists : TokenType("∃")
    object Forall : TokenType("∀")
    object And : TokenType("∧")
    object Or : TokenType("∨")
    object Implies : TokenType("→")
    object Iff : TokenType("↔")
    object Eq : TokenType("=")
    object Neq : TokenType("≠")
    object Not : TokenType("¬")

    // MSO signature
    object E : TokenType("E")

    // MSO variables
    data class SetVariable(val identifier: String) : TokenType(identifier)
    data class ElementVariable(val identifier: String) : TokenType(identifier)

    // punctuation
    object LParen : TokenType("(")
    object RParen : TokenType(")")
    object Comma : TokenType(",")

    object EOF : TokenType("EOF")

    final override fun toString() = repr
}
