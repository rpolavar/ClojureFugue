package org.tm.music;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import org.tm.music.SingleNoteChannel.Note;

public class Guesswork {
    private static String readFromConsole() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String response = "";
        try {
            response = in.readLine().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static void guessNotes() {
        SingleNoteChannel snc = new SingleNoteChannel();
        char scaleType;
        System.out.println("Do you want major scale (m) or natural minor scale (n) or harmonic minor (h)?");
        scaleType = readFromConsole().charAt(0);
        int sizeOfScale;
        System.out.println("Do you deal with full scale (8) or partial scale (5)?");
        sizeOfScale = Integer.parseInt(readFromConsole());
        System.out.println("How many notes do you want to guess at a time?");
        int scaleLength = Integer.parseInt(readFromConsole());
        int[] i = new int[8];
        switch (scaleType) {
        case 'm':
            int[] temp = { 1, 3, 5, 6, 8, 10, 12, 13 };
            i = temp;
            break;
        case 'n':
            int[] tempn = { 1, 3, 4, 6, 8, 9, 11, 13 };
            i = tempn;
            break;
        case 'h':
            int[] temph = { 1, 3, 4, 6, 8, 9, 12, 13 };
            i = temph;
            break;
        }
        char[] c = { 'c', 'd', 'e', 'f', 'g', 'a', 'b', 'c' };
        Random r = new Random(System.currentTimeMillis());
        Note[] n = new Note[4];
        for (int j = 0; j < 4; j++)
            n[j] = snc.new Note(i[j] + 60, 1.0);
        snc.play(n);
        for (int j = 0; j < 4; j++)
            n[j] = snc.new Note(i[j + 4] + 60, 1.0);
        snc.play(n);
        for (int j = 0; j < 4; j++)
            n[j] = snc.new Note(i[7 - j] + 60, 1.0);
        snc.play(n);
        for (int j = 0; j < 4; j++)
            n[j] = snc.new Note(i[3 - j] + 60, 1.0);
        snc.play(n);

        n = new Note[scaleLength];
        char[] a = new char[scaleLength];
        while (true) {
            for (int j = 0; j < scaleLength; j++) {
                int x = r.nextInt(sizeOfScale);
                n[j] = snc.new Note(i[x] + 60, 1.0);
                a[j] = c[x];
            }
            snc.play(n);
            String s = readFromConsole().trim();
            String t = new String(a);
            if (t.equals(s))
                System.out.println("Right!");
            else
                for (int j = 0; j < scaleLength; j++)
                    System.out.print(" " + a[j]);
            System.out.println();
        }
    }

    public static void main(String[] args) throws Exception {
        guessNotes();
        /*
         * InputStream is = snc.getClass().getResourceAsStream("gaathaarahe.txt"); Note[] notes = snc.readNotesFromStream(is);
         * snc.play(notes, 0);
         */

    }

}
