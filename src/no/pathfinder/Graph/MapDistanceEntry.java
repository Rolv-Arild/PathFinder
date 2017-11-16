package no.pathfinder.Graph;

import static no.pathfinder.Graph.Graph.INFINITY;

/**
 * A map distance entry represents the result of a map graph search.
 *
 * @author Rolv-Arild Braaten
 * @version 1.2.0
 * @since 0.2.0
 */
public class MapDistanceEntry extends DistanceEntry {

    long fScore = INFINITY;
    Road road;

    MapDistanceEntry(int index, int start) {
        super(index, start);
    }

    void update(long time, DistanceEntry prev, long fScore) {
        super.update(time, prev);
        this.fScore = fScore;
    }

    void setRoad(Road road) {
        this.road = road;
    }

    @Override
    public int compareTo(DistanceEntry o) {
        MapDistanceEntry m = (MapDistanceEntry) o;
        int comp = Long.compare(this.fScore, m.fScore);
        if (comp != 0) return comp;
        return super.compareTo(o);
//        return this.road.compareTo(m.road);
    }
}