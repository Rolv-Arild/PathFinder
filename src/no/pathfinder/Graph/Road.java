package no.pathfinder.Graph;

/**
 * Road class is planned to be used in map graph for representing edges.
 *
 *
 * @author Rolv-Arild Braaten
 * @version 1.2.0
 * @since 1.2.0
 */
public class Road implements Comparable<Road> {

    long length;
    int speedLimit;
    long timeToTravel;

    public Road(long length, int speedLimit) {
        this.length = length;
        this.speedLimit = speedLimit;
        this.timeToTravel = 360 * length / speedLimit;
    }

    public long getLength() {
        return length;
    }

    public int getSpeedLimit() {
        return speedLimit;
    }

    public long getTimeToTravel() {
        return timeToTravel;
    }

    Road add(Road r) {
        long len = this.length+r.length;
        Road n = new Road(len, -1);
        n.timeToTravel = this.timeToTravel+r.timeToTravel;
        return n;
    }

    @Override
    public int compareTo(Road o) {
        int comp = Long.compare(this.timeToTravel, o.timeToTravel);
        if (comp != 0) return comp;
        return Long.compare(this.length, o.length);
    }
}
