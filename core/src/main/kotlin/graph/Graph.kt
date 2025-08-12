package graph

/**
 * A simple, finite, undirected graph
 */
class Graph() {
    constructor(vertices: Set<GraphVertex>, edges: Set<GraphEdge>) : this() {
        for (vertex in vertices) {
            addVertex(vertex)
        }
        for (edge in edges) {
            addEdge(edge)
        }
    }

    private val _vertices: MutableSet<GraphVertex> = mutableSetOf()
    private val _edges: MutableSet<GraphEdge> = mutableSetOf()
    private val _adj: MutableMap<GraphVertex, MutableSet<GraphVertex>> = mutableMapOf()

    /**
     * [Vertex][GraphVertex] set of the graph
     */
    val vertices: Set<GraphVertex>
        get() = _vertices

    /**
     * [Edge][GraphEdge] set of the graph.
     */
    val edges: Set<GraphEdge>
        get() = _edges

    /**
     * Adjacency list of the graph, mapping each vertex to a set of adjacent vertices
     */
    val adj: Map<GraphVertex, Set<GraphVertex>>
        get() = _adj

    val allVertexSubsets: Sequence<Set<GraphVertex>>
        get() = sequence {
            for (mask in 0 until (1L shl _vertices.size)) {
                val set = mutableSetOf<GraphVertex>()
                for ((index, vertex) in _vertices.withIndex()) {
                    if ((mask shr index) and 1L == 1L) {
                        set.add(vertex)
                    }
                }
                yield(set)
            }
        }

    /**
     * @return `true` if and only if the vertex is part of the graph
     */
    fun hasVertex(vertex: GraphVertex): Boolean {
        return vertex in _vertices
    }

    /**
     * @return `true` if and only if the edge is part of the graph
     */
    fun hasEdge(edge: GraphEdge): Boolean {
        return hasEdge(edge.from, edge.to)
    }

    /**
     * @return `true` if and only if there is an edge between the two vertices
     */
    fun hasEdge(from: GraphVertex, to: GraphVertex): Boolean {
        return _adj[from]?.contains(to) == true
    }

    /**
     * Adds a new vertex to the graph (isolated, without any adjacent vertices).
     *
     * @throws IllegalArgumentException If a vertex with the same id already exists
     */
    fun addVertex(vertex: GraphVertex) {
        require(!hasVertex(vertex)) {
            "Vertex '$vertex' already exists"
        }
        _vertices.add(vertex)
        _adj[vertex] = mutableSetOf()
    }

    /**
     * Attempts to add a new vertex to the graph.
     *
     * @return `true` if the vertex was added, `false` otherwise
     * @see addVertex
     */
    fun tryAddVertex(vertex: GraphVertex): Boolean {
        if (hasVertex(vertex)) {
            return false
        }
        _vertices.add(vertex)
        _adj[vertex] = mutableSetOf()
        return true
    }

    /**
     * Adds a new edge to the graph.
     *
     * @throws IllegalArgumentException If either vertex of the edge does not exist, or if an edge already exists between them
     */
    fun addEdge(edge: GraphEdge) {
        addEdge(edge.from, edge.to)
    }

    /**
     * Adds a new edge between two vertices to the graph.
     *
     * @throws IllegalArgumentException If both vertices are the same
     * @throws IllegalArgumentException If either vertex of the edge does not exist, or if an edge already exists between them
     */
    fun addEdge(from: GraphVertex, to: GraphVertex) {
        val edge = GraphEdge(from, to)
        require(!hasEdge(from, to)) {
            "Edge '$edge' already exists"
        }
        require(hasVertex(from) && hasVertex(to)) {
            "Both vertices of edge '$edge' must exist"
        }
        _edges.add(edge)
        _adj[from]!!.add(to)
        _adj[to]!!.add(from)
    }

    /**
     * Attempts to add an edge to the graph.
     *
     * @return `true` if the vertex was added, `false` otherwise
     * @see addEdge
     */
    fun tryAddEdge(edge: GraphEdge): Boolean {
        return tryAddEdge(edge.from, edge.to)
    }

    /**
     * Attempts to add an edge to the graph.
     *
     * @return `true` if the vertex was added, `false` otherwise
     * @see addEdge
     */
    fun tryAddEdge(from: GraphVertex, to: GraphVertex): Boolean {
        if (hasEdge(from, to) || from == to || !hasVertex(from) || !hasVertex(to)) {
            return false
        }
        _edges.add(GraphEdge(from, to))
        _adj[from]!!.add(to)
        _adj[to]!!.add(from)
        return true
    }

    /**
     * Removes a vertex and all its incident edges from the graph.
     *
     * @throws IllegalArgumentException If the vertex does not exist
     */
    fun removeVertex(vertex: GraphVertex) {
        require(hasVertex(vertex)) {
            "Vertex '$vertex' does not exist"
        }
        _vertices.remove(vertex)
        _adj.remove(vertex)
        for (v in _vertices) {
            _adj[v]!!.remove(vertex)
        }
        _edges.removeIf { it.from == vertex || it.to == vertex }
    }

    /**
     * Attempts to remove a vertex from the graph.
     *
     * @return `true` if the vertex was removed, `false` otherwise
     * @see removeVertex
     */
    fun tryRemoveVertex(vertex: GraphVertex): Boolean {
        if (!hasVertex(vertex)) {
            return false
        }
        _vertices.remove(vertex)
        _adj.remove(vertex)
        for (v in _vertices) {
            _adj[v]!!.remove(vertex)
        }
        _edges.removeIf { it.from == vertex || it.to == vertex }
        return true
    }

    /**
     * Removes an edge (but not its incident vertices!) from the graph.
     *
     * @throws IllegalArgumentException If the edge does not exist
     */
    fun removeEdge(edge: GraphEdge) {
        removeEdge(edge.from, edge.to)
    }

    /**
     * Removes an edge between two different vertices (but not the vertices themselves!) from the graph.
     *
     * @throws IllegalArgumentException If both vertices are the same
     * @throws IllegalArgumentException If the edge does not exist
     */
    fun removeEdge(from: GraphVertex, to: GraphVertex) {
        val edge = GraphEdge(from, to)
        require(hasEdge(from, to)) {
            "Edge '$edge' does not exist"
        }
        _adj[to]!!.remove(from)
        _adj[from]!!.remove(to)
        _edges.remove(edge)
    }

    /**
     * Attempts to remove an edge from the graph.
     *
     * @return `true` if the edge was removed, `false` otherwise
     * @see removeEdge
     */
    fun tryRemoveEdge(edge: GraphEdge): Boolean {
        return tryRemoveEdge(edge.from, edge.to)
    }

    /**
     * Attempts to remove an edge between two vertices from the graph.
     *
     * @return `true` if the edge was removed, `false` otherwise
     * @see removeEdge
     */
    fun tryRemoveEdge(from: GraphVertex, to: GraphVertex): Boolean {
        if (!hasEdge(from, to)) {
            return false
        }
        _adj[to]!!.remove(from)
        _adj[from]!!.remove(to)
        _edges.remove(GraphEdge(from, to))
        return true
    }

    /**
     * Eliminates a vertex, i.e. deletes it and connects all of its neighbors pairwise.
     *
     * @throws IllegalArgumentException If the vertex does not exist
     */
    fun eliminateVertex(vertex: GraphVertex) {
        require(hasVertex(vertex)) {
            "Vertex '$vertex' does not exist"
        }
        val neighborhood = _adj[vertex]!!
        for (v1 in neighborhood) {
            for (v2 in neighborhood) {
                if (v1 == v2) continue
                if (!hasEdge(v1, v2)) {
                    addEdge(v1, v2)
                }
            }
        }
        removeVertex(vertex)
    }

    /**
     * Contracts an edge, i.e. removes it and merges the two incident vertices into a new one.
     * The new vertex is adjacent to all vertices that the incident vertices of the edge were adjacent to.
     * The new vertex has the id `edge.from.id + edge.to.id`, followed by as many primes (`'`) as
     * necessary to make the id unique in the graph.
     *
     * @throws IllegalArgumentException If the edge does not exist
     */
    fun contractEdge(edge: GraphEdge) {
        contractEdge(edge.from, edge.to)
    }

    /**
     * Contracts an edge between two different vertices, i.e. removes it and merges the two vertices into a new one.
     * The new vertex is adjacent to all vertices that the incident vertices of the edge were adjacent to.
     * The new vertex has the id `from.id + to.id`, followed by as many primes (`'`) as
     * necessary to make the id unique in the graph.
     *
     * @throws IllegalArgumentException If both vertices are the same
     * @throws IllegalArgumentException If the edge does not exist
     */
    fun contractEdge(from: GraphVertex, to: GraphVertex) {
        val edge = GraphEdge(from, to)
        require(hasEdge(from, to)) {
            "Edge '$edge' does not exist"
        }
        val neighborhood = _adj[from]!! + _adj[from]!! - from - to
        removeVertex(to)
        removeVertex(from)
        var newVertexName = from.id + to.id
        while (hasVertex(GraphVertex(newVertexName))) {
            newVertexName += "'"
        }
        val newVertex = GraphVertex(newVertexName)
        addVertex(newVertex)
        for (v in neighborhood) {
            addEdge(newVertex, v)
        }
    }
}
