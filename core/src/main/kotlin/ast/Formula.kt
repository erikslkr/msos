package ast

import lexer.Span
import mso.Variable
import mso.Relation

enum class Quantifier(private val repr: String) {
    EXISTENTIAL("∃"),
    UNIVERSAL("∀");

    override fun toString(): String = repr
}

enum class BinaryConnective(private val repr: String) {
    CONJUNCTION("∧"),
    DISJUNCTION("∨"),
    IMPLICATION("→"),
    BIIMPLICATION("↔");

    override fun toString(): String = repr
}

enum class UnaryConnective(private val repr: String) {
    NEGATION("¬");

    override fun toString(): String = repr
}

enum class BinaryOperator(private val repr: String) {
    EQ("="),
    NEQ("≠");

    override fun toString(): String = repr
}

sealed class Formula {
    data class QuantifiedFormula(
        val quantifier: Pair<Quantifier, Span>,
        val variable: Pair<Variable, Span>,
        val innerFormula: Pair<Formula, Span>,
    ) : Formula() {
        override fun toString(): String = "$quantifier$variable($innerFormula)"
    }

    data class BinaryFormula(
        val connective: Pair<BinaryConnective, Span>,
        val left: Pair<Formula, Span>,
        val right: Pair<Formula, Span>,
    ) : Formula() {
        override fun toString(): String = "($left) $connective ($right)"
    }

    data class UnaryFormula(
        val connective: Pair<UnaryConnective, Span>,
        val innerFormula: Pair<Formula, Span>,
    ) : Formula() {
        override fun toString(): String = "$connective($innerFormula)"
    }

    data class BinaryPredicate(
        val operator: Pair<BinaryOperator, Span>,
        val left: Pair<Variable, Span>,
        val right: Pair<Variable, Span>,
    ) : Formula() {
        override fun toString(): String = "$left $operator $right"
    }

    data class RelationPredicate(
        val relation: Pair<Relation, Span>,
        val arguments: List<Pair<Variable, Span>>,
    ) : Formula() {
        override fun toString(): String = "$relation(${arguments.joinToString(",")})"
    }
}
