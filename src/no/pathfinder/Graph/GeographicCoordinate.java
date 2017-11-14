package no.pathfinder.Graph;

import static java.lang.Math.*;

/**
 * Created by Rolv-Arild on 09.11.2017.
 */
public class GeographicCoordinate {

    private static final double EARTH_RADIUS = 6371008; // average earth radius in meters

    private final double lat;
    private final double lon;

    public GeographicCoordinate(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public double lat() {
        return lat;
    }

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

    public double dist(GeographicCoordinate c) {
        double b1 = rad(this.lat);
        double b2 = rad(c.lat);
        double l1 = rad(this.lon);
        double l2 = rad(c.lon);
        return 2 * EARTH_RADIUS * asin(sqrt(sin2(b1, b2) + cos(b1)*cos(b2)*sin2(l1, l2)));
    }

    public static void main(String[] args) {
        GeographicCoordinate g1 = new GeographicCoordinate(0.0, 0.0);
        GeographicCoordinate g2 = new GeographicCoordinate(0.1, 0.1);
        System.out.println(g1.dist(g2));
    }
}
