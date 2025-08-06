package ast

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
        val quantifier: Quantifier,
        val variable: Variable,
        val innerFormula: Formula,
    ) : Formula() {
        override fun toString(): String = "$quantifier$variable($innerFormula)"
    }

    data class BinaryFormula(
        val connective: BinaryConnective,
        val left: Formula,
        val right: Formula,
    ) : Formula() {
        override fun toString(): String = "($left) $connective ($right)"
    }

    data class UnaryFormula(
        val connective: UnaryConnective,
        val innerFormula: Formula,
    ) : Formula() {
        override fun toString(): String = "$connective($innerFormula)"
    }

    data class BinaryPredicate(
        val operator: BinaryOperator,
        val left: Variable,
        val right: Variable,
    ) : Formula() {
        override fun toString(): String = "$left $operator $right"
    }

    data class RelationPredicate(
        val relation: Relation,
        val arguments: List<Variable>,
    ) : Formula() {
        override fun toString(): String = "$relation(${arguments.joinToString(",")})"
    }
}
