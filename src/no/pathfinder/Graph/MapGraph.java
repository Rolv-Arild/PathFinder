package no.pathfinder.Graph;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * A graph to represent intersections with coordinates with roads inbetween.
 * @author Rolv-Arild Braaten
 * @version 1.0.0
 * @since 0.2.0
 */
public class MapGraph<T> extends Graph<GeographicCoordinate, T> {


    private void fill(ArrayList<MapDistanceEntry> vList, PriorityQueue<MapDistanceEntry> Q, int start) {
        for (int i = 0; i < vertices.size(); i++) {
            MapDistanceEntry v = new MapDistanceEntry(i);
            if (i == start) {
                v.update(0, null);
                Q.add(v);
            }
            vList.add(v);
        }
    }

    private void visitVertices(ArrayList<MapDistanceEntry> vList, PriorityQueue<MapDistanceEntry> Q, MapDistanceEntry v, int goal) {
        for (Edge n : vertices.get(v.index).edges) {
            long alt = v.dist + n.weight;
            MapDistanceEntry to = vList.get(n.toIndex);
            if (alt < to.dist) {
                to.update(alt, v, alt + heuristic(vertexValue(to.index), vertexValue(goal)));
                Q.add(to);
            }
        }
    }

    private long heuristic(GeographicCoordinate g1, GeographicCoordinate g2) {
        return (long) (360000*g1.dist(g2)/10.0); // time to drive straight distance at 10km/h
    }

    /**
     * Finds the shortest distance between two places using the A* algorithm.
     *
     * @param start the index of the start vertex.
     * @param end the index of the end vertex.
     * @return the shortest distance between start and end.
     */
    @Override
    public long distance(int start, int end) {
        ArrayList<MapDistanceEntry> vList = new ArrayList<>(vertices.size());
        PriorityQueue<MapDistanceEntry> Q = new PriorityQueue<>();

        fill(vList, Q, start);

        while (!Q.isEmpty()) {
            MapDistanceEntry u = Q.remove();
            if (u.index == end || u.dist == INFINITY) break;
            visitVertices(vList, Q, u, end);
        }
        return vList.get(end).dist;
    }

    /**
     * Finds the path between two nodes using the A* algorithm.
     *
     * @param start the index of the starting vertex.
     * @param end   the index of the ending vertex.
     * @return an array containing the indices of the vertices in the path.
     */
    @Override
    public int[] path(int start, int end) {
        if (start == end) return new int[]{start, end};
        ArrayList<MapDistanceEntry> vList = new ArrayList<>(vertices.size());
        PriorityQueue<MapDistanceEntry> Q = new PriorityQueue<>();

        fill(vList, Q, start);

        while (!Q.isEmpty()) {
            MapDistanceEntry u = Q.remove();
            if (u.index == end || u.dist == INFINITY) break;
            visitVertices(vList, Q, u, end);
        }
        MapDistanceEntry v = vList.get(end);
        if (v.dist == INFINITY) return null;
        int[] path = new int[Math.toIntExact(v.dist + 1)]; // max number of vertices

        int i = 0;
        while (v.prev != null) {
            path[i] = v.index;
            v = (MapDistanceEntry) v.prev;
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
     * Finds the shortest distance between two places using Dijkstra's algorithm.
     *
     * @param start the index of the start vertex.
     * @param end the index of the end vertex.
     * @return the shortest distance between start and end.
     */
    public long Dijkstrance(int start, int end) {
        return super.distance(start, end);
    }


    private class MapDistanceEntry extends DistanceEntry {
        
        long fScore = INFINITY;
        
        MapDistanceEntry(int index) {
            super(index);
        }
        
        void update(long dist, DistanceEntry prev, long fScore) {
            super.update(dist, prev);
            this.fScore = fScore;
        }

        @Override
        public int compareTo(DistanceEntry o) {
            MapDistanceEntry m = (MapDistanceEntry) o;
            return Long.compare(this.fScore, m.fScore);
        }
    }
}
