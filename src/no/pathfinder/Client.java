package no.pathfinder;

import no.pathfinder.Graph.GeographicCoordinate;
import no.pathfinder.Graph.MapGraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Client {

    public static MapGraph<Integer> graphFromFiles(File nodes, File edges) throws IOException {
        FileReader frN = new FileReader(nodes);
        BufferedReader brN = new BufferedReader(frN);

        FileReader frE = new FileReader(edges);
        BufferedReader brE = new BufferedReader(frE);

        MapGraph<Integer> graph = new MapGraph<>();

        String ns = brN.readLine();
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

        String es = brE.readLine();
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

        return graph;
    }


    public static void main(String[] args) throws IOException {
        File nodeFile = new File("resource/noder.txt");
        File edgeFile = new File("resource/kanter.txt");

        System.out.println("Loading graph...");
        MapGraph<Integer> skand = graphFromFiles(nodeFile, edgeFile);

        long time = System.nanoTime();
        long a = skand.Dijkstrance(347370, 430916);
        System.out.println("Dijkstra: " + (System.nanoTime()-time));

        time = System.nanoTime();
        long b = skand.distance(347370, 430916);
        System.out.println("A*: " + (System.nanoTime()-time));

        System.out.println("Finding distance...");
        System.out.println(skand.distance(37774, 18058)); // 31552
        System.out.println(skand.distance(347370, 430916)); // 2226149
        System.out.println(skand.distance(0, 4426215)); // 7799071
    }
}
