package com.viviose.musicscratchpad

import java.util.*

/**
 * Created by Patrick on 2/3/2016.
 */
class Note(var x: Float, yC: Float, acc: Int) {
    var y: Float = 0.toFloat()
    internal var interval = DensityMetrics.spaceHeight
    internal var subInt = interval / 2
    internal var subSubInt = subInt / 2
    var name: NoteName?
    var rhythm: Double = 0.toDouble()
    var octave = Octave.octave
    internal var accidental: Int = acc
    internal var SHARPS = arrayOf(arrayOf(NoteName.f, NoteName.fs), arrayOf(NoteName.c, NoteName.cs), arrayOf(NoteName.g, NoteName.gs), arrayOf(NoteName.d, NoteName.ds), arrayOf(NoteName.a, NoteName.`as`), arrayOf(NoteName.e, NoteName.f), arrayOf(NoteName.b, NoteName.c))
    internal var FLATS = arrayOf(arrayOf(NoteName.b, NoteName.`as`), arrayOf(NoteName.e, NoteName.ds), arrayOf(NoteName.a, NoteName.gs), arrayOf(NoteName.d, NoteName.cs), arrayOf(NoteName.g, NoteName.fs), arrayOf(NoteName.c, NoteName.b), arrayOf(NoteName.f, NoteName.e))

    enum class NoteName {
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
        `as`,
        b
    }

    private val altoStandardNotes: Array<NoteName?> = arrayOf(NoteName.c, NoteName.d, NoteName.e, NoteName.f, NoteName.g, NoteName.a, NoteName.b, NoteName.c, NoteName.d, NoteName.e, NoteName.f, NoteName.g, NoteName.a, NoteName.b, NoteName.c)
    private val trebleStandardNotes: Array<NoteName?> = arrayOf(NoteName.b, NoteName.c, NoteName.d, NoteName.e, NoteName.f, NoteName.g, NoteName.a, NoteName.b, NoteName.c, NoteName.d, NoteName.e, NoteName.f, NoteName.g, NoteName.a, NoteName.b)
    private val bassStandardNotes: Array<NoteName?> = arrayOf(NoteName.d, NoteName.e, NoteName.f, NoteName.g, NoteName.a, NoteName.b, NoteName.c, NoteName.d, NoteName.e, NoteName.f, NoteName.g, NoteName.a, NoteName.b, NoteName.c, NoteName.d)

    inner class NotePosn(internal var noteName: NoteName?, internal var yLower: Float, internal var yUpper: Float, internal var octave: Int)

    private val ALTO_POSN = ArrayList<NotePosn>()

    private fun getNotePosn(y: Float, clef: Clef): ArrayList<NotePosn> {
        var iterArray : Array<NoteName?> = arrayOfNulls<NoteName>(9)
        var res: ArrayList<NotePosn> = ArrayList()
        when (clef) {
            Clef.ALTO -> {
                iterArray = altoStandardNotes
                res = ALTO_POSN
            }
            Clef.TREBLE -> {
                iterArray = trebleStandardNotes
                res = ALTO_POSN
                Octave.octave = 5
            }
            Clef.BASS -> {
                iterArray = bassStandardNotes
                res = ALTO_POSN
                Octave.octave = 4
            }
        }
        for (i in 14 downTo -1 + 1) {
            val yL = interval * 8 - subInt - subInt * (i + 1)
            val yU = interval * 8 - subInt * (i + 1)
            var o = octave
            if (clef == Clef.ALTO) {
                if (i < 7) {
                    if (i == 0) {
                        o = octave - 2
                    } else {
                        o = octave - 1
                    }
                } else if (i == 14) {
                    o = octave + 1
                }
            } else if (clef == Clef.TREBLE) {
                if (i < 8) {

                    o = octave - 1
                } else if (i == 0) {
                    o = octave - 2
                }
            } else if (clef == Clef.BASS) {
                if (i < 6) {
                    o = octave - 2
                } else if (i < 13 && i >= 6) {
                    o = octave - 1
                }

            }

            res.add(NotePosn(iterArray[i], yL, yU, o))
        }
        return res
    }

    private fun getNoteFromPosn(y: Float, clef: Clef): NoteName? {
        val noteList = getNotePosn(y, clef)
        for (i in noteList.indices) {
            if (y > noteList[i].yLower && y <= noteList[i].yUpper) {
                octave = noteList[i].octave
                return noteList[i].noteName
            }
        }
        octave += 1
        return NoteName.c
    }


    private fun accidental(note: NoteName, accidental: Int): NoteName {
        if (accidental == 1) {
            for (sharpNote in SHARPS) {
                if (note == sharpNote[0] && note != NoteName.b && note != NoteName.e) {
                    return sharpNote[1]
                }
            }
        }
        if (accidental == -1) {
            for (flatNote in FLATS) {
                if (note == flatNote[0]) {
                    return flatNote[1]
                }
            }
        }
        return note
    }

    private fun keyAccidental(note: NoteName): NoteName {
        if (Key.COUNT == 0) {
            return note
        }
        if (Key.SHARP) {

            for (i in 0..Key.COUNT - 1) {
                if (note == SHARPS[i][0]) {
                    accidental = 1
                    return SHARPS[i][1]

                }
            }
        } else {
            for (i in 0..Key.COUNT - 1){
                if (note == FLATS[i][0]){
                    accidental = -1
                    return FLATS[i][1]

                }
            }
        }
        return note
    }


    init {
        y = snapNoteY(yC)
        name = accidental(keyAccidental(getNoteFromPosn(snapNoteY(yC) - DensityMetrics.getToolbarHeight(), ClefSetting.clef)!!), acc)
        rhythm = LastRhythm.value

    }


    //Change this to a rounding statement
    private fun snapNoteY(y: Float): Float {

        var snapY = 0f
        for (i in 0..12) {
            if (interval * i - subSubInt < y && interval * i + subSubInt >= y) {
                snapY = interval * i + DensityMetrics.getToolbarHeight()
                break
            } else if (interval * i + subInt - subSubInt < y && interval * i + subInt + subSubInt >= y) {
                snapY = interval * i + subInt + DensityMetrics.getToolbarHeight()
                break
            }
        }
        return snapY
    }


}
