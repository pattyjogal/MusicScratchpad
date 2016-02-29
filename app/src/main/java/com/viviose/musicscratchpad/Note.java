package com.viviose.musicscratchpad;

import android.util.DisplayMetrics;

import java.util.ArrayList;

/**
 * Created by Patrick on 2/3/2016.
 */
public class Note{
    public float x;
    public float y;
    float interval = DensityMetrics.spaceHeight;
    float subInt = DensityMetrics.spaceHeight / 2;
    float subSubInt = subInt / 2;
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
            float yL = (interval * 8 - subInt) - subInt * i + DensityMetrics.getToolbarHeight();
            float yU = (interval * 8) - subInt * i + DensityMetrics.getToolbarHeight();
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
            if (y - DensityMetrics.getToolbarHeight() > noteList.get(i).yLower && y - DensityMetrics.getToolbarHeight() <= noteList.get(i).yUpper){
                octave = noteList.get(i).octave;
                return noteList.get(i).noteName;
            }
        }
        octave += 1;
        return NoteName.c;
    }

    private NoteName keyAccidental(NoteName note){
        if (Key.COUNT == 0){
            return note;
        }
        if (Key.SHARP){
            NoteName[][] sharps = { {NoteName.f, NoteName.fs},
                                    {NoteName.c, NoteName.cs},
                                    {NoteName.g, NoteName.gs},
                                    {NoteName.d, NoteName.ds},
                                    {NoteName.a, NoteName.as},
                                    {NoteName.e, NoteName.f},
                                    {NoteName.b, NoteName.c}};
            for (int i = 0; i < sharps.length; i++){
                if (note == sharps[i][0]){
                    return sharps[i][1];
                }
            }
        }
        return note;
    }




    public Note(float xC, float yC){
        x = xC;
        y = snapNoteY(yC);
        name = getNoteFromPosn(yC, ClefSetting.clef);

    }


//Change this to a rounding statement
    private float snapNoteY(float y) {

        float snapY = 0;
        for (int i = 1; i < 9; i++) {
            if (interval * i - subSubInt < y && interval * i + subSubInt >= y) {
                snapY = interval * i;
            }else if(interval * i + subInt - subSubInt < y && interval * i + subInt + subSubInt >= y){
                snapY = interval * i + subInt;
            }
        }
        return snapY;
    }


}
