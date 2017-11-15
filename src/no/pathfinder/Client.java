package no.pathfinder;

import javafx.application.Application;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import no.pathfinder.Graph.GeographicCoordinate;
import no.pathfinder.Graph.MapGraph;
import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;

import java.awt.*;
import java.io.*;
import java.util.HashMap;

public class Client extends Application {

    private static final File nodeFile = new File("resource/noder.txt");
    private static final File edgeFile = new File("resource/kanter.txt");
    private static final File placeFile = new File("resource/plasser.dat");
    private static final MapGraph<Integer> skand = graphFromFiles(nodeFile, edgeFile);
    private static final HashMap<String, Integer> places = mapFromFile(placeFile);

    private static HashMap<String, Integer> mapFromFile(File placeFile) {
        try {
            FileInputStream fis = new FileInputStream(placeFile);
            ObjectInputStream ois = new ObjectInputStream(fis);
            return (HashMap<String, Integer>) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }


    public static MapGraph<Integer> graphFromFiles(File nodes, File edges) {
        try {
            FileReader frN = new FileReader(nodes);
            BufferedReader brN = new BufferedReader(frN);

            FileReader frE = new FileReader(edges);
            BufferedReader brE = new BufferedReader(frE);

            MapGraph<Integer> graph = new MapGraph<>();

            brN.readLine(); // first line contains number of nodes
            String ns;
            while ((ns = brN.readLine()) != null) {
                double[] numbers = new double[3];
                int c = 0;
                for (int i = 0; i < ns.length(); i++) {
                    if (ns.charAt(i) != ' ') {
                        if (Character.isDigit(ns.charAt(i))) {
                            numbers[c] *= 10;
                            numbers[c] += Integer.parseInt(String.valueOf(ns.charAt(i)));
                        }
                    } else if (i > 0 && ns.charAt(i - 1) != ' ') {
                        c++;
                    }
                }
                graph.addVertex(new GeographicCoordinate(numbers[1] / 1E7, numbers[2] / 1E7));
            }

            brE.readLine(); // first line contains number of edges
            String es;
            while ((es = brE.readLine()) != null) {
                int[] numbers = new int[3];
                int c = 0;
                for (int i = 0; i < es.length(); i++) {
                    if (Character.isDigit(es.charAt(i))) {
                        numbers[c] *= 10;
                        numbers[c] += Integer.parseInt(String.valueOf(es.charAt(i)));
                    } else if (i > 0 && es.charAt(i - 1) != ' ') {
                        c++;
                        if (c == 3) break;
                    }
                }
                graph.addEdge(numbers[0], numbers[1], numbers[2]);
            }

            brN.close();
            brE.close();
            frN.close();
            frE.close();
            return graph;
        } catch (Exception e){
            return null;
        }
    }

    private static void nodeMap() throws IOException {
        FileReader fr = new FileReader("resource/L7Skandinavia-navn.txt");
        BufferedReader br = new BufferedReader(fr);

        HashMap<String, Integer> map = new HashMap<>();
        String s;
        while ((s = br.readLine()) != null) {
            String[] split = s.split("\"");
            int index = -1;
            for (String s1 : split) {
                if (s1.length() == 0) continue;
                if (s1.matches("\\d+\\s*")) {
                    index = Integer.parseInt(s1.trim());
                } else {
                    if (index < 0) continue;
                    String s2 = s1;
                    int i = 1;
                    while (map.containsKey(s2)) {
                        s2 = s1+i;
                        i++;
                    }
                    map.put(s2, index);
                }
            }
        }
        FileOutputStream fos = new FileOutputStream("resource/plasser.dat");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(map);
        br.close();
        fr.close();
        oos.close();
        fos.close();
    }

    public static void main(String[] args) throws IOException {
//        nodeMap();
        if (skand == null) throw new NullPointerException("asdasd");

        // GUI launcher
        launch(args);
        System.exit(0);

        // Testing against Dijkstra
        long time = System.nanoTime();
        long b = skand.distance(347370, 143917);
        System.out.println("A*: " + (System.nanoTime()-time)/1E9);

        time = System.nanoTime();
        long a = skand.Dijkstrance(347370, 143917);
        System.out.println("Dijkstra: " + (System.nanoTime()-time)/1E9);

        if (a != b) System.out.println("Error: " + a + " != " + b);
        else System.out.println(a);

        System.out.println("Finding distance...");
        System.out.println(skand.distance(37774, 18058)); // 31552
        System.out.println(skand.distance(347370, 430916)); // 2226149
        System.out.println(skand.distance(0, 4426215)); // 7799071



        int now = 123456;
        int last1 = 0;
        int last2;
        do {
            last2 = last1;
            last1 = now;
            now = skand.furthestPoint(now);
            System.out.println(now);
        } while (last2 != now);
        System.out.println(now + ", " + last1); // 1537587, 1557335

        long di1 = skand.distance(now, last1);
        long di2 = skand.Dijkstrance(now, last1);
        System.out.println(di1 + ", " + di2 + ", " + (di1 == di2));
    }

    private static void showPath(int start, int end, JMapViewer mapViewer) {
        mapViewer.removeAllMapMarkers();
        mapViewer.removeAllMapPolygons();
        assert skand != null;
        int[] p = skand.path(start, end);
        GeographicCoordinate g = skand.vertexValue(start);
        mapViewer.addMapMarker(new MapMarkerDot(g.lat(), g.lon()));
        if (p != null) {
            for (int i = 1; i < p.length; i++) {
                GeographicCoordinate g1 = skand.vertexValue(p[i - 1]);
                GeographicCoordinate g2 = skand.vertexValue(p[i]);
                Coordinate c1 = new Coordinate(g1.lat(), g1.lon());
                Coordinate c2 = new Coordinate(g2.lat(), g2.lon());
                mapViewer.addMapPolygon(new MapPolygonImpl(c1, c2, c2));
            }
        }
        GeographicCoordinate h = skand.vertexValue(end);
        mapViewer.addMapMarker(new MapMarkerDot(h.lat(), h.lon()));
        mapViewer.setDisplayToFitMapMarkers();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        TextField text1 = new TextField("start");

        TextField text2 = new TextField("end");

        Button button = new Button("Find path");

        Text result = new Text();

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(0, 0, 0, 0));

        grid.add(text1, 1, 0);
        grid.add(text2, 2, 0);
        grid.add(button, 1, 1);
        grid.add(result, 2, 1);

        JMapViewer mapViewer = new JMapViewer();
        mapViewer.setPreferredSize(new Dimension(1000, 1000));
        SwingNode node = new SwingNode();
        node.setContent(mapViewer);
        grid.add(node, 0, 2, 4, 4);

        button.onActionProperty().setValue(event -> {
            synchronized (mapViewer) {
                assert places != null;
                assert skand != null;
                Integer i1;
                try {
                    i1 = Integer.parseInt(text1.getText());
                } catch (NumberFormatException e) {
                    i1 = places.get(text1.getText());
                }
                Integer i2;
                try {
                    i2 = Integer.parseInt(text2.getText());
                } catch (NumberFormatException e) {
                    i2 = places.get(text2.getText());
                }
                if (i1 == null || i2 == null) {
                    result.setText("Invalid");
                } else {
                    showPath(i1, i2, mapViewer);
                    result.setText(timeString(skand.distance(i1, i2)));
                }
            }
        });


        Scene scene = new Scene(grid, 1920, 1080);

        primaryStage.setTitle("Kart");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private String timeString(long time) {
        if (time == Long.MAX_VALUE) return "Ingen vei";
        time /= 100;
        String sec = time%60 + "s";
        time /= 60;
        String min = (time != 0) ? time%60 + "m," : "";
        time /= 60;
        String hours = (time != 0) ? time%24 + "h," : "";
        time /= 24;
        String days = (time != 0) ? time + "d," : "";
        return days + hours + min + sec;
    }
}
