package no.pathfinder.Graph;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * A graph to represent intersections with coordinates with roads inbetween.
 *
 * @author Rolv-Arild Braaten
 * @version 1.1.0
 * @since 0.2.0
 */
public class MapGraph<T> extends Graph<GeographicCoordinate, T> {

    private void fill(ArrayList<MapDistanceEntry> vList, PriorityQueue<MapDistanceEntry> Q, int start) {
        for (int i = 0; i < vertices.size(); i++) {
            MapDistanceEntry v = new MapDistanceEntry(i, start);
            if (i == start) {
                v.update(0, null);
                Q.add(v);
            }
            vList.add(v);
        }
    }

    private void visitVertices(ArrayList<MapDistanceEntry> vList, PriorityQueue<MapDistanceEntry> Q, MapDistanceEntry v, int goal) {
        for (Edge n : vertices.get(v.index).edges) {
            long alt = v.weight + n.weight;
            MapDistanceEntry to = vList.get(n.toIndex);
            if (alt < to.weight) {
                to.update(alt, v, alt + heuristic(vertexValue(to.index), vertexValue(goal)));
                Q.add(to);
            }
        }
    }

    private long heuristic(GeographicCoordinate g1, GeographicCoordinate g2) {
        return (long) (2.769230769230769 * g1.dist(g2)); // time to drive straight distance at 130km/h (x meters / (130 km/h) =* 2.769*x centiseconds)
    }

    /**
     * Finds the shortest distance between two places using the A* algorithm.
     *
     * @param start the index of the start vertex.
     * @param end the index of the end vertex.
     * @return the shortest distance between start and end.
     */
    public MapDistanceEntry AStar(int start, int end) {
        ArrayList<MapDistanceEntry> vList = new ArrayList<>(vertices.size());
        PriorityQueue<MapDistanceEntry> Q = new PriorityQueue<>();

        fill(vList, Q, start);

        int c = 0;
        while (!Q.isEmpty()) {
            MapDistanceEntry u = Q.remove();
            c++;
            if (u.index == end || u.weight == INFINITY) break;
            visitVertices(vList, Q, u, end);
        }
//        System.out.println(c);
        return vList.get(end);
    }
}
