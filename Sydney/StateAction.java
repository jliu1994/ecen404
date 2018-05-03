package com.example.jason.tamusmartinsole;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by jason on 2/17/2018.
 */

public class StateAction {
    final static int SITTING = 1;
    final static int STANDING =2;
    final static int WALKING =3;


    static Integer StateAction(int current_state, ArrayList<Integer> current_matrix, StringBuilder conclusion){
        conclusion.setLength(0);
        //ArrayList<String> Correctness = new ArrayList<String>(Collections.nCopies(5,"a"));
        int posture = 0;
        double halfPosture = 0;
        double topPressure = (current_matrix.get(0) + current_matrix.get(1) + current_matrix.get(2) ) / 3;
        double heelPressure = (current_matrix.get(3) + current_matrix.get(4) + current_matrix.get(5)) / 3;
        double pressureDiff = ((Math.abs(topPressure - heelPressure)) / (Math.max(topPressure,heelPressure) +1)) * 100;
        double archPressure = current_matrix.get(3);
        double rightPressure = current_matrix.get(4);
        double leftRightDiff = ((Math.abs(archPressure - rightPressure)) / (Math.max(archPressure,rightPressure) + 1)) * 100;
        switch (current_state) {
            case SITTING:
                //sitting means ignore data , havent figured out ignore
                posture = 100;
                break;
            case STANDING:
                //compare to standing matrix
                if (pressureDiff <= 20) {
                    //foot is balanced
                    halfPosture = 0;
                    conclusion.append("foot is top bottom balanced") ;
                    String a = "foot is top bottom balanced";
                }
                else if (topPressure > heelPressure) {
                    //foot is too top heavy
                    halfPosture = pressureDiff/2;
                    conclusion.append("foot has too much weight on front of foot") ;
                    String b ="foot has too much weight on front of foot";
                }
                else {
                    //foot has to much weight on heel
                    halfPosture = pressureDiff/2;
                    conclusion.append("foot has too much weight on heel");
                    String c ="foot has too much weight on heel";
                }

                if (archPressure > rightPressure) {
                    //pronated, flat foot, too much weight on arch
                    posture = (int)(halfPosture + leftRightDiff/2);
                    conclusion.append("foot pronates, too much weight on arch");
                    String d ="foot pronates, too much weight on arch";
                }
                else if (archPressure < rightPressure) {
                    //more pressure on non arch side supinator
                    posture =(int) (halfPosture + leftRightDiff/2);
                    conclusion.append("foot supinators, too much weight on non arch") ;
                    String e ="foot supinators, too much weight on non arch";
                }
                else {
                    //left right balanced
                    posture =(int) (halfPosture + 0);
                    conclusion.append("foot is left right balanced") ;
                    String f ="foot is left right balanced";
                }
                break;
            case WALKING:
                //compare to walking
                if (archPressure > rightPressure) {
                    //pronated, flat foot, too much weight on arch
                    posture = (int)(0 + leftRightDiff/2);
                    conclusion.append("foot pronates, too much weight on arch");
                }
                else if (archPressure < rightPressure) {
                    //more pressure on non arch side supinator
                    posture =(int) (0 + leftRightDiff/2);
                    conclusion.append("foot supinators, too much weight on non arch") ;
                }
                else {
                    //left right balanced
                    posture = 0;
                    conclusion.append("foot is left right balanced") ;
                }
                break;
            //default
            default:
                //ignore data
                posture = 300;
        }
        //cout << halfPosture << " "  << pressureDiff << " " << topPressure << " " << heelPressure << endl;
        //cout << leftRightDiff << " " << archPressure << " " << rightPressure << endl;
        return (100 - posture);
    }
}