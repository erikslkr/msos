package mso

import graph.GraphVertex

class Assignment {
    private val elementAssignments = HashMap<String, GraphVertex>()
    private val setAssignments = HashMap<String, Set<GraphVertex>>()

    fun assignElement(variable: ElementVariable, value: GraphVertex) {
        elementAssignments[variable.name] = value
    }

    fun unassignElement(variable: ElementVariable) {
        elementAssignments.remove(variable.name)
    }

    fun assignSet(variable: SetVariable, value: Set<GraphVertex>) {
        setAssignments[variable.name] = value
    }

    fun unassignSet(variable: SetVariable) {
        setAssignments.remove(variable.name)
    }

    fun getElement(variable: ElementVariable): GraphVertex {
        return elementAssignments[variable.name] ?: throw RuntimeException("Unassigned variable '$variable'")
    }

    fun getSet(variable: SetVariable): Set<GraphVertex> {
        return setAssignments[variable.name] ?: throw RuntimeException("Unassigned variable '$variable'")
    }
}
