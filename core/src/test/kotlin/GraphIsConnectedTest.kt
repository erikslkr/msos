import graph.Graph
import graph.GraphVertex
import org.junit.jupiter.api.Assertions.*
import kotlin.test.Test

class GraphIsConnectedTest {
    @Test
    fun `K_1 is connected`() {
        val graph = Graph()
        graph.addVertex(GraphVertex("a"))
        assertTrue(graph.isConnected())
    }

    @Test
    fun `P_3 is connected`() {
        val graph = Graph()
        graph.addVertex(GraphVertex("a"))
        graph.addVertex(GraphVertex("b"))
        graph.addVertex(GraphVertex("c"))
        graph.addEdge(GraphVertex("a"), GraphVertex("b"))
        graph.addEdge(GraphVertex("b"), GraphVertex("c"))
        assertTrue(graph.isConnected())
    }

    @Test
    fun `N_2 is disconnected`() {
        val graph = Graph()
        graph.addVertex(GraphVertex("a"))
        graph.addVertex(GraphVertex("b"))
        assertFalse(graph.isConnected())
    }

    @Test
    fun `K_{2,3} is connected`() {
        val graph = Graph()
        graph.addVertex(GraphVertex("a"))
        graph.addVertex(GraphVertex("b"))
        graph.addVertex(GraphVertex("1"))
        graph.addVertex(GraphVertex("2"))
        graph.addVertex(GraphVertex("3"))
        graph.addEdge(GraphVertex("a"), GraphVertex("1"))
        graph.addEdge(GraphVertex("a"), GraphVertex("2"))
        graph.addEdge(GraphVertex("a"), GraphVertex("3"))
        graph.addEdge(GraphVertex("b"), GraphVertex("1"))
        graph.addEdge(GraphVertex("b"), GraphVertex("2"))
        graph.addEdge(GraphVertex("b"), GraphVertex("3"))
        assertTrue(graph.isConnected())
    }
}
