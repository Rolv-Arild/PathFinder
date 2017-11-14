package no.pathfinder.Graph;

import java.util.*;

/**
 * Created by Rolv-Arild on 09.11.2017.
 */
public class Graph<V, E> {

    protected static final long INFINITY = Long.MAX_VALUE;

    protected ArrayList<Vertex> vertices;

    public Graph() {
        this.vertices = new ArrayList<>();
    }

    public Graph(int size) {
        this.vertices = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            addVertex();
        }
    }

    public Graph(List<V> data) {
        this.vertices = new ArrayList<>(data.size());
        for (V datum : data) {
            addVertex(datum);
        }
    }

    public int indexOf(V val) {
        for (int i = 0; i < vertices.size(); i++) {
            if (getVertex(i).data.equals(val)) return i;
        }
        return -1;
    }
    
    private Vertex getVertex(int index) {
        return vertices.get(index);
    }

    /**
     * Changes the value of a vertex.
     *
     * @param index the index of the vertex.
     * @param value the value of the vertex.
     */
    public void setVertexValue(int index, V value) {
        vertices.get(index).data = value;
    }

    public V vertexValue(int index) {
        return getVertex(index).data;
    }

    public void addVertex(V datum) {
        vertices.add(new Vertex(datum));
    }

    public void addVertex() {
        vertices.add(new Vertex(null));
    }

    @SafeVarargs
    public final void addVertices(V... data) {
        for (V datum : data) {
            addVertex(datum);
        }
    }

    private E edgeValue(Vertex start, Vertex end) {
        for (Edge edge : start.edges) {
            if (getVertex(edge.toIndex).equals(end)) return edge.data;
        }
        return null;
    }

    public E edgeValue(int startIndex, int endIndex) {
        return edgeValue(getVertex(startIndex), getVertex(endIndex));
    }

    public void addEdge(int startIndex, int endIndex, E data, long weight) {
        getVertex(startIndex).addEdge(new Edge(data, weight, endIndex));
    }

    public void addEdge(int i1, int i2, long weight) {
        addEdge(i1, i2, null, weight);
    }

    public void addEdge(int i1, int i2, E data) {
        addEdge(i1, i2, data, INFINITY);
    }

    public void addEdge(int i1, int i2) {
        addEdge(i1, i2, null, INFINITY);
    }

    private Edge getEdge(Vertex start, Vertex end) {
        for (Edge edge : start.edges) {
            if (getVertex(edge.toIndex).equals(end)) return edge;
        }
        return null;
    }

    private Edge getEdge(int startIndex, int endIndex) {
        return getEdge(getVertex(startIndex), getVertex(endIndex));
    }

    private E removeEdge(Vertex start, Vertex end) {
        for (int i = 0; i < start.edges.size(); i++) {
            if (getVertex(start.edges.get(i).toIndex).equals(end)) {
                return start.edges.remove(i).data;
            }
        }
        return null;
    }

    private E removeEdge(int startIndex, int endIndex) {
        return removeEdge(getVertex(startIndex), getVertex(endIndex));
    }

    private boolean adjacent(Vertex start, Vertex end) {
        for (Edge edge : start.edges) {
            if (getVertex(edge.toIndex).equals(end)) return true;
        }
        return false;
    }

    public boolean adjacent(int startIndex, int endIndex) {
        return adjacent(getVertex(startIndex), getVertex(endIndex));
    }


    private void fill(ArrayList<DistanceEntry> vList, PriorityQueue<DistanceEntry> Q, int start) {
        for (int i = 0; i < vertices.size(); i++) {
            DistanceEntry v = new DistanceEntry(i);
            if (i == start) {
                v.update(0, null);
                Q.add(v);
            }
            vList.add(v);
        }
    }

    private void visitVertices(ArrayList<DistanceEntry> vList, PriorityQueue<DistanceEntry> Q, DistanceEntry v) {
        for (Edge n : getVertex(v.index).edges) {
            long alt = v.dist + n.weight;
            DistanceEntry to = vList.get(n.toIndex);
            if (alt < to.dist) {
                to.update(alt, v);
                Q.add(to);
            }
        }
    }

    public long distance(int start, int end) {
        ArrayList<DistanceEntry> vList = new ArrayList<>(vertices.size());
        PriorityQueue<DistanceEntry> Q = new PriorityQueue<>();

        fill(vList, Q, start);

        while (!Q.isEmpty()) {
            DistanceEntry u = Q.remove();
            if (u.index == end || u.dist == INFINITY) break;
            visitVertices(vList, Q, u);
        }
        return vList.get(end).dist;
    }

    public int closest(int start, V target) {
        ArrayList<DistanceEntry> vList = new ArrayList<>(vertices.size());
        PriorityQueue<DistanceEntry> Q = new PriorityQueue<>();

        fill(vList, Q, start);

        while (!Q.isEmpty()) {
            DistanceEntry u = Q.remove();
            if (getVertex(u.index).data.equals(target)) return u.index;
            if (u.dist == INFINITY) break;
            visitVertices(vList, Q, u);
        }
        return -1;
    }


    /**
     * Returns an array of the distances from the start node to each node in the graph,
     * or Integer.MAX_VALUE if there is no connection.
     *
     * @param start the index of the starting vertex.
     * @return an array of the distances from the start node to each node in the graph,
     * or -1 if there is no connection.
     */
    public long[] BFS(int start) {
        boolean[] checked = new boolean[vertices.size()];
        ArrayList<Integer> queue = new ArrayList<>();
        long[] distances = new long[vertices.size()];

        Arrays.fill(distances, INFINITY);
        distances[start] = 0;
        checked[start] = true;

        queue.add(start);

        while (!queue.isEmpty()) {
            int current = queue.remove(0);
            
            for (Edge n : getVertex(current).edges) {
                if (!checked[n.toIndex]) {
                    checked[n.toIndex] = true;
                    queue.add(n.toIndex);
                    distances[n.toIndex] = distances[current] + n.weight;
                }
            }
        }
        return distances;
    }

    /**
     * Returns an array of the path from one node to another found using a breadth-first search.
     *
     * @param start the index of the starting vertex.
     * @param end   the index of the end vertex.
     * @return an array of the path from one node to another.
     */
    public int[] BFS(int start, int end) {
        ArrayList<Integer> queue = new ArrayList<>();
        boolean[] checked = new boolean[vertices.size()];
        long[] distances = new long[vertices.size()];
        int[] last = new int[vertices.size()];

        Arrays.fill(distances, INFINITY);

        Arrays.fill(last, -1);
        checked[start] = true;
        distances[start] = 0;
        queue.add(start);

        while (!queue.isEmpty()) {
            int current = queue.remove(0);
            if (current == end) break; // end vertex has been found

            for (Edge n : getVertex(current).edges) {
                if (!checked[n.toIndex]) {
                    checked[n.toIndex] = true;
                    queue.add(n.toIndex);
                    distances[n.toIndex] = distances[current] + 1;
                    last[n.toIndex] = current;
                }
            }
        }
        if (distances[end] == INFINITY) return null;

        int[] path = new int[Math.toIntExact(distances[end] + 1)];
        int index = end;
        for (int i = path.length - 1; i >= 0; i--) {
            path[i] = index;
            index = last[index];
        }
        path[0] = start;

        return path;
    }


    /**
     * Returns an array of the distances from the start node to each node in the graph,
     * or Integer.MAX_VALUE if there is no connection.
     *
     * @param start the index of the starting vertex.
     * @return an array of the distances from the start node to each node in the graph,
     * or Integer.MAX_VALUE if there is no connection.
     */
    public long[] DFS(int start) {
        Stack<Integer> queue = new Stack<>();
        boolean[] discovered = new boolean[vertices.size()];
        long[] distances = new long[vertices.size()];

        Arrays.fill(distances, INFINITY);

        distances[start] = 0;

        queue.push(start);
        while (!queue.isEmpty()) {
            int current = queue.pop();
            if (!discovered[current]) {
                discovered[current] = true;

                for (Edge n : getVertex(current).edges) {
                    queue.push(n.toIndex);
                    if (!discovered[n.toIndex]) {
                        distances[n.toIndex] = distances[current] + 1;
                    }
                }
            }
        }
        return distances;
    }

    /**
     * Returns an array of the path from one node to another found using a depth-first search.
     *
     * @param start the index of the starting vertex.
     * @param end   the index of the end vertex.
     * @return an array of the path from one node to another, or null if there is none.
     */
    public int[] DFS(int start, int end) {
        Stack<Integer> queue = new Stack<>();
        boolean[] discovered = new boolean[vertices.size()];
        int[] distances = new int[vertices.size()];
        int[] last = new int[vertices.size()];

        queue.push(start);
        while (!queue.isEmpty()) {
            int current = queue.pop();
            if (current == end) break;
            if (!discovered[current]) {
                discovered[current] = true;

                for (Edge n : getVertex(current).edges) {
                    queue.push(n.toIndex);
                    if (!discovered[n.toIndex]) {
                        distances[n.toIndex] = distances[current] + 1;
                        last[n.toIndex] = current;
                    }
                }
            }
        }
        if (distances[end] == INFINITY) return null;

        int[] path = new int[distances[end] + 1];
        int index = end;
        for (int i = path.length - 1; i >= 0; i--) {
            path[i] = index;
            index = last[index];
        }
        path[0] = start;

        return path;
    }


    /**
     * Finds the distances from a vertex to all other vertices using Dijkstra's algorithm.
     *
     * @param start the starting node.
     * @return an array containing the distances to every vertex in the graph.
     */
    public long[] Dijkstra(int start) {
        ArrayList<DistanceEntry> vList = new ArrayList<>(vertices.size());
        PriorityQueue<DistanceEntry> Q = new PriorityQueue<>();

        fill(vList, Q, start);

        while (!Q.isEmpty()) {
            DistanceEntry u = Q.remove();
            if (u.dist == INFINITY) break;
            visitVertices(vList, Q, u);
        }
        long[] dist = new long[vertices.size()];

        for (int i = 0; i < vList.size(); i++) {
            dist[i] = vList.get(i).dist;
        }
        return dist;
    }


    /**
     * Finds the path between two nodes using Dijkstra's algorithm.
     *
     * @param start the index of the starting vertex.
     * @param end   the index of the ending vertex.
     * @return an array containing the indices of the vertices in the path.
     */
    public int[] Dijkstra(int start, int end) {
        if (start == end) return new int[]{start, end};       
        ArrayList<DistanceEntry> vList = new ArrayList<>(vertices.size());
        PriorityQueue<DistanceEntry> Q = new PriorityQueue<>();

        fill(vList, Q, start);

        while (!Q.isEmpty()) {
            DistanceEntry u = Q.remove();
            if (u.index == end || u.dist == INFINITY) break;
            visitVertices(vList, Q, u);
        }
        DistanceEntry v = vList.get(end);
        if (v.dist == INFINITY) return null;
        int[] path = new int[Math.toIntExact(v.dist + 1)]; // max number of vertices
        
        int i = 0;
        while (v.prev != null) {
            path[i] = v.index;
            v = v.prev;
            i++;
        }
        int[] path2 = new int[i + 1];
        for (int j = 0; j < path2.length; j++) {
            path2[i - j] = path[j];
        }
        path2[0] = start;
        return path2;
    }

    /**
     * Sorts the graph topologically.
     *
     * @return an array with a suggested topographical order for the graph.
     */
    public int[] topologicalSort() {
        Stack<Integer> L = new Stack<>();
        boolean[] discovered = new boolean[vertices.size()];
        boolean[] temp = new boolean[vertices.size()];
        for (int i = 0; i < discovered.length; i++) {
            visit(i, L, discovered, temp);
        }
        int[] rest = new int[L.size()];
        for (int j = 0; j < rest.length; j++) {
            rest[j] = L.pop();
        }
        return rest;
    }

    /* Recursive function for topological sort */
    private void visit(int i, Stack<Integer> L, boolean[] perm, boolean[] temp) {
        if (perm[i]) return;
        if (temp[i]) throw new IllegalStateException("Graph is not DAG");
        temp[i] = true;
        for (Edge n : getVertex(i).edges) {
            visit(n.toIndex, L, perm, temp);
        }
        perm[i] = true;
        L.push(i);
    }


    public int followPath(int startIndex, E... path) {
        loop:
        for (E e : path) {
            for (Edge edge : getVertex(startIndex).edges) {
                if (edge.data.equals(e)) {
                    startIndex = edge.toIndex;
                    continue loop;
                }
            }
            return -1;
        }
        return startIndex;
    }

    public int size() {
        return vertices.size();
    }

    public int[] leafIndexes() {
        ArrayList<Integer> indexes = new ArrayList<>();
        for (int i = 0; i < vertices.size(); i++) {
            if (vertices.get(i).edges.isEmpty()) indexes.add(i);
        }
        int[] ints = new int[indexes.size()];
        for (int i = 0; i < ints.length; i++) {
            ints[i] = indexes.get(i);
        }
        return ints;
    }


    protected class Vertex {
        V data;
        LinkedList<Edge> edges;

        Vertex(V data) {
            this.data = data;
            this.edges = new LinkedList<>();
        }

        void addEdge(Edge e) {
            edges.add(e);
        }
    }

    protected class Edge {
        E data;
        long weight;
        int toIndex;

        Edge(E data, long weight, int toIndex) {
            this.data = data;
            this.weight = weight;
            this.toIndex = toIndex;
        }
    }

    protected class DistanceEntry implements Comparable<DistanceEntry> {
        int index;
        long dist = INFINITY;
        DistanceEntry prev = null;

        DistanceEntry(int index) {
            this.index = index;
        }

        void update(long dist, DistanceEntry prev) {
            this.dist = dist;
            this.prev = prev;
        }

        @Override
        public int compareTo(DistanceEntry o) {
            return Long.compare(dist, o.dist);
        }

        @Override
        public String toString() {
            return String.valueOf(index);
        }
    }
}
