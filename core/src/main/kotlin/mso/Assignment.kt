package mso

import graph.Vertex

class Assignment {
    private val elementAssignments = HashMap<String, Vertex>()
    private val setAssignments = HashMap<String, Set<Vertex>>()

    fun assignElement(variable: ElementVariable, value: Vertex) {
        elementAssignments[variable.name] = value
    }

    fun unassignElement(variable: ElementVariable) {
        elementAssignments.remove(variable.name)
    }

    fun assignSet(variable: SetVariable, value: Set<Vertex>) {
        setAssignments[variable.name] = value
    }

    fun unassignSet(variable: SetVariable) {
        setAssignments.remove(variable.name)
    }

    fun getElement(variable: ElementVariable): Vertex {
        return elementAssignments[variable.name] ?: throw RuntimeException("Unassigned variable '$variable'")
    }

    fun getSet(variable: SetVariable): Set<Vertex> {
        return setAssignments[variable.name] ?: throw RuntimeException("Unassigned variable '$variable'")
    }
}
