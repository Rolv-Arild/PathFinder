package no.pathfinder.Graph;

import static no.pathfinder.Graph.Graph.INFINITY;


/**
 * A distance entry represents the result of a map graph search.
 *
 * @author Rolv-Arild Braaten
 * @version 1.2.0
 * @since 0.2.0
 */
public class DistanceEntry implements Comparable<DistanceEntry> {

    int start;
    int index;
    long weight = INFINITY;
    DistanceEntry prev = null;

    DistanceEntry(int index, int start) {
        this.index = index;
        this.start = start;
    }

    void update(long weight, DistanceEntry prev) {
        this.weight = weight;
        this.prev = prev;
    }

    /**
     * Returns an array of the indexes of the vertices in the shortest
     * path to the vertex at the index of this distance entry
     *
     * @return an array of the indexes of the vertices on the path to this vertex.
     */
    public int[] getPath() {
        DistanceEntry n = this;
        int c = 0;
        while (n != null) { // first find path length
            n = n.prev;
            c++;
        }
        int[] path = new int[c];
        c = 0;
        n = this;
        while (n != null) { // then fill with values
            path[c] = n.index;
            c++;
            n = n.prev;
        }
        return path;
    }

    /**
     * Getter for weight.
     * @return the weight of this distance entry
     */
    public long getWeight() {
        return weight;
    }

    /**
     * Getter for start.
     * @return the index of the starting vertex of this distance entry.
     */
    public int getStart() {
        return start;
    }

    /**
     * Getter for index.
     * @return the index of the corresponding vertex to this distance entry.
     */
    public int getIndex() {
        return index;
    }

    @Override
    public int compareTo(DistanceEntry o) {
        return Long.compare(weight, o.weight);
    }

    @Override
    public String toString() {
        return String.valueOf(index);
    }
}
