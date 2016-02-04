package com.viviose.musicscratchpad;

/**
 * Created by Patrick on 2/3/2016.
 */
public class Note {
    public float x;
    public float y;
    //NoteName name;
    public enum NoteName{
        C,
        Cs,
        D,
        Ds,
        E,
        F,
        G,
        Gs,
        A,
        As,
        B
    }
    public enum Clef{
        ALTO,
        TREBLE,
        BASS
    }
    public Note(float xC, float yC){
        x = xC;
        y = snapNoteY(yC);
    }



    private float snapNoteY(float y) {
        float snapY;

        if (450 <= y && 550 >= y) {
            return 500;
        }else if (550 < y && 650 >= y){
            return 600;
        }else if (650 < y && 750 >= y){
            return 700;
        }else if (750 < y && 850 >= y){
            return 800;
        }else if (850 < y && 950 >= y){
            return 900;
        }else if (950 < y && 1050 >= y){
            return 1000;
        }else if (1050 < y && 1150 >= y){
            return 1100;
        }else if (1150 < y && 1250 >= y){
            return 1200;
        }else if (1250 < y && 1350 >= y){
            return 1300;
        }

        return -100;
    }


}
