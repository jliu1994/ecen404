package com.example.jason.tamusmartinsole;

import java.util.ArrayList;
import java.util.Collections;

public class ColorMatrix {
    static ArrayList<String> colorMatrix(ArrayList<Integer> P){
        // P needs to be the function input
        ArrayList<String> C = new ArrayList<String>(Collections.nCopies(6,"a"));
        ArrayList<Integer> M = new ArrayList<Integer>(P);

        for(int i=0;i<6;i++){
            if(M.get(i) <= 73)
                C.set(i, "blue"); // blue
            else if(M.get(i) > 73 && M.get(i) <= 253)
                C.set(i,"green"); // green
            else if(M.get(i) > 253 && M.get(i) <= 343)
                C.set(i, "yellow"); // yellow
            else if(M.get(i) > 343 && M.get(i) <= 433)
                C.set(i, "orange"); // orange
            else if(M.get(i) > 433 && M.get(i) <= 523)
                C.set(i, "red"); //red
            else
                C.set(i, "white"); // default, white circle : nothing
        }

        return C;
    }
}