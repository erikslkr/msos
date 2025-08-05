package lexer

sealed class Token(private val repr: String) {
    // MSO operators
    object Exists : Token("∃")
    object Forall : Token("∀")
    object And : Token("∧")
    object Or : Token("∨")
    object Implies : Token("→")
    object Iff : Token("↔")
    object Eq : Token("=")
    object Neq : Token("≠")
    object Not : Token("¬")

    // MSO signature
    object E : Token("E")

    // MSO variables
    data class SetVariable(val identifier: String) : Token(identifier)
    data class ElementVariable(val identifier: String) : Token(identifier)

    // punctuation
    object LParen : Token("(")
    object RParen : Token(")")
    object Comma : Token(",")

    object EOF : Token("EOF")

    final override fun toString() = repr
}
