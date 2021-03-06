package com.aware.plugin.notificationdiary.ContentAnalysis;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by aku on 01/12/16.
 */
public class Cluster {
    private final String TAG = "Cluster";
    final Node centroid;
    public int max_depth = 0;
    private HashMap<Integer, ArrayList<Node>> nodes;
    private ArrayList<String> words;

    public Cluster() {
        this.centroid = new Node("post_generated");
        this.nodes = new HashMap<>();
        this.words = new ArrayList<>();
    }

    public Cluster(Node n) {
        this.centroid = n;
        this.nodes = new HashMap<>();
        this.words = new ArrayList<>();
    }

    public void addNode(int d, Node n) {
        if (d < 1) return;
        if (!nodes.keySet().contains(d)) {
            nodes.put(d, new ArrayList<Node>());
            max_depth = d;
        }
        nodes.get(d).add(n);
        words.add(n.value);
    }

    // getString nodes with distance d from centroid
    public ArrayList<Node> getNodes(int d) {
        //Log.d(TAG, "getNodes(" + d + ") " + centroid.toString());
        ArrayList<Node> result = new ArrayList<>();
        if (d > max_depth) return result;
        if (d == 0) {
            //Log.d(TAG, "level 1 iteration, returning centroid : " + centroid);
            result.add(centroid);
            return result;
        } else {
            //Log.d(TAG, "level " + d + " returning list with size: " + nodes.getString(d).size());
            return nodes.get(d);
        }
    }

    // getString all nodes
    public ArrayList<Node> getNodes() {
        ArrayList<Node> result = new ArrayList<>();
        result.add(centroid);
        for (int i = 1; i <= max_depth; i++) {
            for (Node n : nodes.get(i)) {
                result.add(n);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        String result = "Cluster (" + centroid.value + "): " + words.size() + " words.";

        for (Integer level : nodes.keySet()) {
            Log.d(TAG, "level ("+level+"): " + nodes.get(level).size() + " nodes");
        }
        return result;
    }

}
