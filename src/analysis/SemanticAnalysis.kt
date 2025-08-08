package analysis

import ast.Formula
import mso.EdgeRelation
import mso.ElementVariable
import mso.SetVariable

// TODO: Positions
data class SemanticError(val message: String)

class SemanticAnalyzer {
    private val variables = ArrayDeque<String>()

    private val errors = mutableListOf<SemanticError>()
    private val warnings = mutableListOf<SemanticError>()

    /**
     * Semantically analyzes the [formula][Formula] and returns `true` if it is valid, `false` otherwise.
     * Collects [errors] and [warnings].
     */
    fun analyzeFormula(formula: Formula): Boolean {
        return when (formula) {
            is Formula.QuantifiedFormula -> {
                var isValid = true
                if (formula.variable.name in variables) {
                    collectError("Duplicate variable '${formula.variable.name}'")
                    isValid = false
                }
                variables.addLast(formula.variable.name)
                isValid and analyzeFormula(formula.innerFormula).also {
                    variables.removeLast()
                }
            }
            is Formula.BinaryFormula -> {
                analyzeFormula(formula.left) and analyzeFormula(formula.right)
            }
            is Formula.BinaryPredicate -> {
                var isValid = true
                if (formula.left.name !in variables) {
                    collectError("Variable '${formula.left.name}' does not exist")
                    isValid = false
                }
                if (formula.right.name !in variables) {
                    collectError("Variable '${formula.right.name}' does not exist")
                    isValid = false
                }
                when (formula.left) {
                    is ElementVariable -> {
                        if (formula.right !is ElementVariable) {
                            collectError("Cannot compare element variable '${formula.left.name}' to set variable '${formula.right.name}'")
                            isValid = false
                        }
                    }
                    is SetVariable -> {
                        if (formula.right !is SetVariable) {
                            collectError("Cannot compare set variable '${formula.left.name}' to element variable '${formula.right.name}'")
                            isValid = false
                        }
                    }
                }
                isValid
            }
            is Formula.RelationPredicate -> {
                when (formula.relation) {
                    EdgeRelation -> {
                        var isValid = true
                        if (formula.arguments.size != 2) {
                            collectError("Expected 2 arguments for 'E', but found ${formula.arguments.size}")
                            isValid = false
                        }
                        for (argument in formula.arguments) {
                            if (argument !is ElementVariable) {
                                collectError("Argument for 'E' must be element variable, not set variable")
                                isValid = false
                            }
                            if (argument.name !in variables) {
                                collectError("Variable '${argument.name}' does not exist")
                                isValid = false
                            }
                        }
                        if (formula.arguments.size == 2 && formula.arguments[0].name == formula.arguments[1].name) {
                            collectWarning("'E(${formula.arguments[0].name}, ${formula.arguments[0].name})' is always false")
                        }
                        isValid
                    }
                    is SetVariable -> {
                        var isValid = true
                        if (formula.relation.name !in variables) {
                            collectError("Variable '${formula.relation.name}' does not exist")
                            isValid = false
                        }
                        if (formula.arguments.size != 1) {
                            collectError("Expected 1 argument for '${formula.relation.name}', but found ${formula.arguments.size}")
                            isValid = false
                        }
                        for (argument in formula.arguments) {
                            if (argument !is ElementVariable) {
                                collectError("Argument for '${formula.relation.name}' must be element variable, not set variable")
                                isValid = false
                            }
                            if (argument.name !in variables) {
                                collectError("Variable '${argument.name}' does not exist")
                                isValid = false
                            }
                        }
                        isValid
                    }
                }
            }
            is Formula.UnaryFormula -> {
                analyzeFormula(formula.innerFormula)
            }
        }
    }

    private fun collectError(message: String) {
        errors.add(SemanticError(message))
    }

    private fun collectWarning(message: String) {
        warnings.add(SemanticError(message))
    }

    fun printErrors() {
        val red = "\u001b[31m"
        val bold = "\u001b[1m"
        val reset = "\u001b[0m"
        for (error in errors) {
            println("${red}${bold}ERROR: ${reset}${red}${error.message}${reset}")
        }
    }

    fun printWarnings() {
        val yellow = "\u001b[33m"
        val bold = "\u001b[1m"
        val reset = "\u001b[0m"
        for (warning in warnings) {
            println("${yellow}${bold}WARNING: ${reset}${yellow}${warning.message}${reset}")
        }
    }
}
