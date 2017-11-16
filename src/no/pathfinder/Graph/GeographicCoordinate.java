package no.pathfinder.Graph;

import static java.lang.Math.*;

/**
 * A class for storing geographic coordinate info and finding distances between them.
 *
 * @author Rolv-Arild
 * @version 1.1.0
 * @since 0.2.0
 */
public class GeographicCoordinate {

    private static final double EARTH_RADIUS = 6371008; // average earth radius in meters

    private final double lat;
    private final double lon;

    /**
     * Creates a geographic coordinate with the given latitude and longitude.
     *
     * @param lat the latitude of this geographic coordinate.
     * @param lon the longitude of this geographic coordinate.
     */
    public GeographicCoordinate(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    /**
     * Getter for latitude.
     * @return the latitude of this geographic coordinate.
     */
    public double lat() {
        return lat;
    }

    /**
     * Getter for longitude.
     * @return the longitude of this geographic coordinate.
     */
    public double lon() {
        return lon;
    }

    private double rad(double deg) {
        return deg * PI / 180.0;
    }

    private double sin2(double a1, double a2) {
        double sin = sin((a1-a2)/2.0);
        return sin*sin;
    }

    /**
     * Uses Haversine's formula to
     * @param c the geographic coordinate to find the distance to.
     * @return the distance from to {@code c} in meters.
     */
    public double dist(GeographicCoordinate c) {
        double b1 = rad(this.lat);
        double b2 = rad(c.lat);
        double l1 = rad(this.lon);
        double l2 = rad(c.lon);
        return 2 * EARTH_RADIUS * asin(sqrt(sin2(b1, b2) + cos(b1)*cos(b2)*sin2(l1, l2)));
    }

    @Override
    public String toString() {
        return "(" + lat + "," + lon + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeographicCoordinate that = (GeographicCoordinate) o;
        return Double.compare(that.lat, lat) == 0 && Double.compare(that.lon, lon) == 0;
    }
}
