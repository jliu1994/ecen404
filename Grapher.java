package com.example.jason.tamusmartinsole;
import java.util.ArrayList;
import java.util.Collections;
/**
 * Created by jason on 2/17/2018.
 */

public class Grapher {
    static ArrayList<Integer> graph = new ArrayList<Integer>(Collections.nCopies(5,0));

    static ArrayList<Integer> grapher(int posture){
        graph.add(posture);
        graph.remove(0);
        return graph;
    }
}
