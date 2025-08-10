package mso

sealed interface Variable {
    val name: String
}

sealed interface Relation {
    val name: String
    val argCount: Int
}

data class ElementVariable(override val name: String) : Variable {
    override fun toString(): String = name
}

data class SetVariable(override val name: String) : Variable, Relation {
    override val argCount: Int = 1
    override fun toString(): String = name
}

object EdgeRelation : Relation {
    override val argCount: Int = 2
    override val name: String = "E"
    override fun toString(): String = name
}
