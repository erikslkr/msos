package graph

/**
 * Creates an edgeless graph with [n] vertices (also known as N_n).
 * The vertices are labeled 1 through n.
 *
 * @throws IllegalArgumentException If n < 0
 */
fun edgelessGraph(n: Int): Graph {
    require(n >= 0) {
        "A graph must have at least 0 vertices"
    }
    val graph = Graph()
    for (i in 1 .. n) {
        graph.addVertex(GraphVertex("$i"))
    }
    return graph
}

/**
 * Creates a complete graph with [n] vertices (also known as K_n),
 * i.e. a graph where all vertices are connected pairwise.
 * The vertices are labeled 1 through n.
 *
 * @throws IllegalArgumentException If n < 0
 */
fun completeGraph(n: Int): Graph {
    val graph = edgelessGraph(n)
    for (i in 1 .. n) {
        for (j in (i + 1) .. n) {
            graph.addEdge(GraphVertex("$i"), GraphVertex("$j"))
        }
    }
    return graph
}

/**
 * Creates a complete bipartite graph with partitions of size [m] and [n] (also known as K_{m,n}).
 * The vertices in the partitions are labeled 1 through m, and 1' through n', respectively.
 *
 * @throws IllegalArgumentException If m < 0 or n < 0
 */
fun completeBipartiteGraph(m: Int, n: Int): Graph {
    require(m >= 0 && n >= 0) {
        "A bipartite graph must have at least 0 vertices per partition"
    }
    val graph = Graph()
    for (i in 1 .. m) {
        graph.addVertex(GraphVertex("$i"))
    }
    for (j in 1 .. n) {
        graph.addVertex(GraphVertex("$j'"))
    }
    for (i in 1 .. m) {
        for (j in 1 .. n) {
            graph.addEdge(GraphVertex("$i"), GraphVertex("$j'"))
        }
    }
    return graph
}

/**
 * Creates a path graph with [n] vertices, i.e. a path of length `n - 1` (also known as P_n).
 * The vertices are labeled 1 through n.
 *
 * @throws IllegalArgumentException If n < 1
 */
fun pathGraph(n: Int): Graph {
    require(n >= 1) {
        "A path graph must have at least 1 vertex"
    }
    val graph = edgelessGraph(n)
    for (i in 1 until n) {
        graph.addEdge(GraphVertex("$i"), GraphVertex("${i + 1}"))
    }
    return graph
}

/**
 * Creates a cycle graph with [n] vertices (also known as C_n).
 * The vertices are labeled 1 through n.
 *
 * @throws IllegalArgumentException If n < 3
 */
fun cycleGraph(n: Int): Graph {
    require(n >= 3) {
        "A cycle graph must have at least 3 vertices"
    }
    val graph = pathGraph(n)
    graph.addEdge(GraphVertex("1"), GraphVertex("$n"))
    return graph
}
