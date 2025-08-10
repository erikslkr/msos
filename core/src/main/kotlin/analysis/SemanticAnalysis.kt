package analysis

import ast.BinaryOperator
import ast.Formula
import lexer.Span
import mso.EdgeRelation
import mso.ElementVariable
import mso.SetVariable

data class SemanticError(val message: String, val span: Span)

class SemanticAnalyzer {
    private val quantifiedVariables = ArrayDeque<String>()
    private val referencedVariables = mutableListOf<String>()

    private val _errors = mutableListOf<SemanticError>()
    private val _warnings = mutableListOf<SemanticError>()

    val errors: List<SemanticError> get() = _errors
    val warnings: List<SemanticError> get() = _warnings

    fun analyzeFormula(formulaWithSpan: Pair<Formula, Span>): Boolean {
        return analyzeFormula(formulaWithSpan.first, formulaWithSpan.second)
    }

    /**
     * Semantically analyzes the [formula][Formula] and returns `true` if it is valid, `false` otherwise.
     * Collects [errors] and [warnings].
     *
     * @param formula The MSO formula (AST) to be analyzed
     * @param span The location of the formula in the input string
     */
    fun analyzeFormula(formula: Formula, span: Span): Boolean {
        return when (formula) {
            is Formula.QuantifiedFormula -> {
                var isValid = true
                if (formula.variable.first.name in quantifiedVariables) {
                    collectError("Duplicate variable '${formula.variable.first.name}'", formula.variable.second)
                    isValid = false
                }
                quantifiedVariables.addLast(formula.variable.first.name)
                val isInnerValid = analyzeFormula(formula.innerFormula)
                quantifiedVariables.removeLast()
                if (formula.variable.first.name !in referencedVariables) {
                    collectWarning("Variable '${formula.variable.first.name}' is never used", formula.variable.second)
                }
                isValid && isInnerValid
            }
            is Formula.BinaryFormula -> {
                analyzeFormula(formula.left) and analyzeFormula(formula.right)
            }
            is Formula.BinaryPredicate -> {
                var isValid = true
                if (formula.left.first.name !in quantifiedVariables) {
                    collectError(
                        "Variable '${formula.left.first.name}' does not exist",
                        formula.left.second
                    )
                    isValid = false
                }
                if (formula.left.first.name == formula.right.first.name) {
                    val name = formula.left.first.name
                    // warning: 'x = x' is always true
                    when (formula.operator.first) {
                        BinaryOperator.EQ -> {
                            collectWarning(
                                "'$name = $name' is always true",
                                span
                            )
                        }
                        BinaryOperator.NEQ -> {
                            collectWarning(
                                "'$name ≠ $name' is always false",
                                span
                            )
                        }
                    }
                } else if (formula.right.first.name !in quantifiedVariables) {
                    collectError(
                        "Variable '${formula.right.first.name}' does not exist",
                        formula.right.second
                    )
                    isValid = false
                }
                when (formula.left.first) {
                    is ElementVariable -> {
                        if (formula.right.first !is ElementVariable) {
                            collectError(
                                "Cannot compare element variable '${formula.left.first.name}' to set variable '${formula.right.first.name}'",
                                span
                            )
                            isValid = false
                        }
                    }
                    is SetVariable -> {
                        if (formula.right.first !is SetVariable) {
                            collectError(
                                "Cannot compare set variable '${formula.left.first.name}' to element variable '${formula.right.first.name}'",
                                span
                            )
                            isValid = false
                        }
                    }
                }
                referencedVariables.add(formula.left.first.name)
                referencedVariables.add(formula.right.first.name)
                isValid
            }
            is Formula.RelationPredicate -> {
                var isValid = true
                val argCount = formula.relation.first.argCount
                if (formula.arguments.size != argCount) {
                    collectError(
                        "Expected $argCount argument${if (argCount != 1) "s" else ""} for '${formula.relation.first.name}', but found ${formula.arguments.size}",
                        formula.arguments.first().second join formula.arguments.last().second
                    )
                    isValid = false
                }
                for ((argVariable, argSpan) in formula.arguments) {
                    if (argVariable !is ElementVariable) {
                        collectError(
                            "Argument for '${formula.relation.first.name}' must be element variable, not set variable",
                            argSpan
                        )
                        isValid = false
                    }
                    if (argVariable.name !in quantifiedVariables) {
                        collectError(
                            "Variable '${argVariable.name}' does not exist",
                            argSpan
                        )
                        isValid = false
                    }
                }
                if (formula.relation.first == EdgeRelation && formula.arguments.size == 2 && formula.arguments[0].first.name == formula.arguments[1].first.name) {
                    collectWarning(
                        "'E(${formula.arguments[0].first.name},${formula.arguments[0].first.name})' is always false",
                        span
                    )
                }
                referencedVariables.add(formula.relation.first.name)
                referencedVariables.addAll(formula.arguments.map { it.first.name })
                isValid
            }
            is Formula.UnaryFormula -> {
                analyzeFormula(formula.innerFormula)
            }
        }
    }

    private fun collectError(message: String, span: Span) {
        _errors.add(SemanticError(message, span))
    }

    private fun collectWarning(message: String, span: Span) {
        _warnings.add(SemanticError(message, span))
    }
}
