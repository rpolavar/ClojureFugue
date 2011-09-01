package org.tm.music;

public class TextToMusic {
    public static void main(String[] args) throws Exception {
        SingleNoteChannel snc = new SingleNoteChannel();
        snc.readAndPlay("c:\\maryMary.txt");
    }
}
