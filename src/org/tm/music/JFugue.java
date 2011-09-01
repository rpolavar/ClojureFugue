package org.tm.music;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.tm.music.SingleNoteChannel.Note;

public class JFugue {
    public static String readFromConsole() {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String response = "";
        try {
            response = in.readLine().trim();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public static void play() {
        SingleNoteChannel snc = new SingleNoteChannel();
        int[] d = { 1, 3, 5, 6, 8, 10, 12, 13 };
        char[] c = { 'c', 'd', 'e', 'f', 'g', 'a', 'b', 'c' };
        System.out.println("Enter notes from a through g without spaces:");
        while (true) {
            String s = readFromConsole();
            Note[] notes = new Note[s.length()];
            s = s.toLowerCase();
            for (int i = 0; i < s.length(); i++) {
                char tempC = s.charAt(i);
                int tempD = -1;
                for (int j = 0; j < c.length; j++)
                    if (tempC == c[j]) {
                        tempD = j;
                        break;
                    }
                notes[i] = snc.new Note(60 + tempD, 1.0);
            }
            snc.play(notes);
            System.out.println("Enter some more notes!");
        }
    }

    public static void main(String[] args) {
        SingleNoteChannel snc = new SingleNoteChannel();
        int[] d = { 1, 3, 5, 6, 8, 10, 12, 13 };
        char[] c = { 'c', 'd', 'e', 'f', 'g', 'a', 'b', 'c' };
        String s = readFromConsole();
        Note[] notes = new Note[s.length()];
        s = s.toLowerCase();
        for (int i = 0; i < s.length(); i++) {
            char tempC = s.charAt(i);
            int tempD = -1;
            for (int j = 0; j < c.length; j++)
                if (tempC == c[j]) {
                    tempD = j;
                    break;
                }
            notes[i] = snc.new Note(60 + tempD, 1.0);
        }
        snc.play(notes);
    }

}
