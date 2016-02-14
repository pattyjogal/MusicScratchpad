package com.viviose.musicscratchpad;

import android.util.DisplayMetrics;

import java.util.ArrayList;

/**
 * Created by Patrick on 2/3/2016.
 */
public class Note{
    public float x;
    public float y;
    NoteName name;
    int octave = Octave.octave;
    public enum NoteName{
        c,
        cs,
        d,
        ds,
        e,
        f,
        fs,
        g,
        gs,
        a,
        as,
        b
    }
    private NoteName[] altoStandardNotes =      {NoteName.c, NoteName.d, NoteName.e, NoteName.f, NoteName.g, NoteName.a, NoteName.b, NoteName.c, NoteName.d, NoteName.e, NoteName.f, NoteName.g, NoteName.a, NoteName.b};
    private NoteName[] trebleStandardNotes =    {NoteName.b, NoteName.c, NoteName.d, NoteName.e, NoteName.f, NoteName.g, NoteName.a, NoteName.b, NoteName.c, NoteName.d, NoteName.e, NoteName.f, NoteName.g, NoteName.a};
    private NoteName[] bassStandardNotes =      {NoteName.d, NoteName.e, NoteName.f, NoteName.g, NoteName.a, NoteName.b, NoteName.c, NoteName.d, NoteName.e, NoteName.f, NoteName.g, NoteName.a, NoteName.b, NoteName.c};

    public class NotePosn{
        NoteName noteName;
        float yLower;
        float yUpper;
        int octave;
        public NotePosn(NoteName n, float yL, float yU, int oct){
            noteName = n;
            yLower = yL;
            yUpper = yU;
            octave = oct;
        }
    }

    private ArrayList ALTO_POSN = new ArrayList();

    private ArrayList<NotePosn> getNotePosn(float y, Clef clef){
        NoteName[] iterArray = new NoteName[9];
        ArrayList<NotePosn> res = new ArrayList();
        switch (clef){
            case ALTO:
                iterArray = altoStandardNotes;
                res = ALTO_POSN;
                break;
            case TREBLE:
                iterArray = trebleStandardNotes;
                res = ALTO_POSN;
                Octave.octave = 5;
                break;
            case BASS:
                iterArray = bassStandardNotes;
                res = ALTO_POSN;
                Octave.octave = 4;
        }
        for (int i = 13; i > -1; i--){
            float yL = 1550 - 100 * i;
            float yU = 1650 - 100 * i;
            int o = octave;
            if (clef == Clef.ALTO){
                if (i < 7){
                    o = octave - 1;
                }
            }else if (clef == Clef.TREBLE){
                if (i < 8){

                    o = octave - 1;
                }
            }else if (clef == Clef.BASS) {
                if (i < 6) {
                    o = octave - 2;
                }else if ( i < 13 && i >= 6){
                    o = octave - 1;
                }

            }

            res.add(new NotePosn(iterArray[i], yL, yU, o));
        }
        return res;
    }

    private NoteName getNoteFromPosn(float y, Clef clef){
        ArrayList<NotePosn> noteList = getNotePosn(y, clef);
        for (int i = 0; i < noteList.size(); i++) {
            if (y > noteList.get(i).yLower && y <= noteList.get(i).yUpper){
                octave = noteList.get(i).octave;
                return noteList.get(i).noteName;
            }
        }
        octave += 1;
        return NoteName.c;
    }




    public Note(float xC, float yC){
        x = xC;
        y = snapNoteY(yC);
        name = getNoteFromPosn(yC, ClefSetting.clef);

    }


//Change this to a rounding statement
    private float snapNoteY(float y) {
        float snapY;

        if (450 <= y && 550 >= y) {
            snapY = 500;
        }else if (550 < y && 650 >= y){
            snapY = 600;
        }else if (650 < y && 750 >= y){
            snapY = 700;
        }else if (750 < y && 850 >= y){
            snapY = 800;
        }else if (850 < y && 950 >= y){
            snapY = 900;
        }else if (950 < y && 1050 >= y){
            snapY = 1000;
        }else if (1050 < y && 1150 >= y){
            snapY = 1100;
        }else if (1150 < y && 1250 >= y){
            snapY = 1200;
        }else if (1250 < y && 1350 >= y) {
            snapY = 1300;
        }else if (350 <= y && 450 >= y){
            snapY = 400;
        }else if (250 <= y && 350 >= y){
            snapY = 300;
        }else if (1350 <= y && 1450 >= y){
            snapY = 1400;
        }else if (1450 <= y && 1550 >= y){
            snapY = 1500;
        }else if (1550 <= y && 1650 >= y){
            snapY = 1600;
        }else{
            snapY = -1000;
        }

        return snapY;
    }


}
