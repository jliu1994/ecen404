package com.example.jason.tamusmartinsole;

import java.util.ArrayList;

/**
 * Created by jason on 2/17/2018.
 */

public class StateDecider {
    final static int SITTING = 1;
    final static int STANDING =2;
    final static int WALKING =3;
    static int stateDecider(int weightonfoot, int heel_toe, int current_state) {
        if (current_state == SITTING) {
            if (weightonfoot < 5) {
                current_state = SITTING;
                //sitting to sitting arrow
            } else { // weightonfoot > 5
                if (heel_toe == 0) {
                    current_state = STANDING;
                    // sitting to standing arrow
                } else {//heel toe = 1
                    current_state = WALKING;
                    // sitting to walking arrow
                }
            }
        } else if (current_state == STANDING) {
            if (weightonfoot < 5) {
                current_state = SITTING;
                // Standing to sitting arrow
            } else { //weightonfoot > 5
                if (heel_toe == 0) {
                    current_state = STANDING;
                    // standing to standing arrow
                } else { //heel toe = 1
                    current_state = WALKING;
                    // standing to walking arrow
                }
            }
        } else if (current_state == WALKING) {
            if (weightonfoot < 5) {
                current_state = SITTING;
                // walking to sitting arrow
            } else { //weightonfoot > 5
                if (heel_toe == 0) {
                    current_state = STANDING;
                    // walking to standing arrow
                } else { // heel_toe = 1
                    current_state = WALKING;
                    // walking to walking arrow
                }
            }
        }
        return current_state;
    }
}