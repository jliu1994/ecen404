package com.example.jason.tamusmartinsole;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by jason on 2/17/2018.
 */

public class Heel_toe {
    static ArrayList<Integer> matrix_1=new ArrayList<Integer>(Collections.nCopies(6,0));
    static ArrayList<Integer> matrix_2=new ArrayList<Integer>(matrix_1);
    static ArrayList<Integer> cur_matrix=new ArrayList<Integer>(matrix_1);

    static int heel_toe (ArrayList<Integer> current_matrix){
        //set matrixes
        matrix_2 = matrix_1;
        matrix_1 = cur_matrix;
        cur_matrix = current_matrix;

        //if [heel, middle, toe] then heel_toe = 1
        if (matrix_2.get(0) == 0 && matrix_2.get(1) == 0 && matrix_2.get(2) == 0 && matrix_2.get(3) == 0 && matrix_2.get(4) >= 0 && matrix_2.get(5) != 0) {//heel
            if (cur_matrix.get(0) != 0 && cur_matrix.get(1) != 0 && cur_matrix.get(2) != 0 && cur_matrix.get(3) >= 0 && cur_matrix.get(4) == 0 && cur_matrix.get(5) == 0 ) {
                //toe
                return 1;
            }
        }

        //if [middle, toe, heel] then heel_toe = 1
        if (cur_matrix.get(0) == 0 && cur_matrix.get(1) == 0 && cur_matrix.get(2) == 0 && cur_matrix.get(3) == 0 && cur_matrix.get(4) >= 0 && cur_matrix.get(5) != 0) {//heel
            if (matrix_1.get(0) != 0 && matrix_1.get(1) != 0 && matrix_1.get(2) != 0 && matrix_1.get(3) >= 0 && matrix_1.get(4) == 0 && matrix_1.get(5) == 0 ) {
                //toe
                return 1;
            }
        }

        //if [toe, heel, middle] then heel_toe = 1
        if (matrix_1.get(0) == 0 && matrix_1.get(1) == 0 && matrix_1.get(2) == 0 && matrix_1.get(3) == 0 && matrix_1.get(4) >= 0 && matrix_1.get(5) != 0) {//heel
            if (matrix_2.get(0) != 0 && matrix_2.get(1) != 0 && matrix_2.get(2) != 0 && matrix_2.get(3) >= 0 && matrix_2.get(4) == 0 && matrix_2.get(5) == 0 ) {
                //toe
                return 1;
            }
        }

        return 0;
    }
}