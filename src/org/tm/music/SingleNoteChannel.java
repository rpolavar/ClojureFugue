package org.tm.music;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Synthesizer;

public class SingleNoteChannel {

    private MidiChannel[] channels;

    private Synthesizer synth;

    private char[] noteNames = { 'c', 'C', 'd', 'D', 'e', 'E', 'f', 'F', 'g', 'G', 'a', 'A', 'b', 'B', 'r' };

    private int[] noteValues = { 0, 1, 2, 3, 4, 5, 5, 6, 7, 8, 9, 10, 11, 12, -1000 };

    private double[] durationNames = { 1, 1.5, 2, 2.5, 4, 4.5, 8, 8.5, 16, 16.5 };

    private double[] durationValues = { 1, 1.5, .5, .75, .25, .375, .125, .188, .063, .094 };

    public SingleNoteChannel() {
        try {
            synth = MidiSystem.getSynthesizer();
            synth.open();
            channels = synth.getChannels();
            // channels[0].programChange(40);
        } catch (MidiUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void play(int[] notes, double d) {
        Note[] n = new Note[notes.length];
        for (int i = 0; i < notes.length; i++)
            n[i] = new Note(notes[i], d);
        play(n);
    }

    public void play(Playable[] notes) {
        for (int i = 0; i < notes.length; i++) {
            notes[i].play();
        }
        // channels[notes[i].cn].allNotesOff();
    }

    private Note[] readNotesFromStream(InputStream is) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line = null;
        ArrayList notes = new ArrayList();
        int note = 0;
        double duration = 0.0;
        int valacity = 0;
        while ((line = br.readLine()) != null) {
            while (true) {
                StringTokenizer st = new StringTokenizer(line);
                if (st.hasMoreTokens()) {
                    note = Integer.parseInt(st.nextToken());
                }
                if (st.hasMoreTokens()) {
                    duration = Double.parseDouble(st.nextToken());
                } else {
                    Note n = new Note(note);
                    notes.add(n);
                    break;
                }
                if (st.hasMoreTokens()) {
                    valacity = Integer.parseInt(st.nextToken());
                } else {
                    Note n = new Note(note, duration);
                    notes.add(n);
                    break;
                }
                Note n = new Note(note, duration, valacity);
                notes.add(n);
                break;
            }
        }

        Note[] result = new Note[notes.size()];
        notes.toArray(result);
        return result;
    }

    public void readAndPlay(String fileName) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line = null;
        List<Playable> notes = new ArrayList<Playable>();
        while ((line = br.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(line);
            while (st.hasMoreTokens()) {
                String token = st.nextToken();
                System.out.println(token);
                if (token.startsWith("{")) {
                    List<String> tokensForChord = new ArrayList<String>();
                    tokensForChord.add(token.substring(1));
                    while (st.hasMoreTokens()) {
                        String t = st.nextToken();
                        if (t.endsWith("}")) {
                            tokensForChord.add(t.substring(0, t.length() - 1));
                            break;
                        }
                        tokensForChord.add(t);
                    }
                    notes.add(buildChord(tokensForChord));
                    continue;
                }
                notes.add(buildNote(token));
            }
        }

        Playable[] result = new Playable[notes.size()];
        notes.toArray(result);
        play(result);
    }

    public List<String> checkForMelodies(List<String> tokens) {
        List<String> result = new ArrayList<String>();
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            int ind = token.indexOf("[");
            if (ind >= 0) {
                String m = token.substring(ind);
                for (int j = i + 1; j < tokens.size(); j++) {
                    String t = tokens.get(j);
                    ind = t.indexOf("]");
                    if (ind >= 0) {
                        m += (" " + t.substring(0, ind + 1));
                        i = j;
                        break;
                    }
                    m += (" " + t);
                }
                result.add(m);
            } else {
                result.add(token);
            }
        }
        return result;
    }

    public Note buildNote(String token) {
        System.out.println("Inside buildNote");
        System.out.println(token);
        int note = ((1 + (token.charAt(0) - '0')) * 12);
        for (int j = 0; j < noteNames.length; j++)
            if (noteNames[j] == token.charAt(1)) {
                note += noteValues[j];
                break;
            }
        int valacityInd = token.indexOf("v");
        int valacity = 70;
        String s = token.substring(2);
        if (valacityInd >= 0) {
            s = token.substring(2, valacityInd);
            valacity = Integer.parseInt(token.substring(valacityInd + 1));
        }
        double temp = Double.parseDouble(s);
        double duration = 0.0;
        for (int j = 0; j < durationNames.length; j++)
            if (durationNames[j] == temp) {
                duration = durationValues[j];
                break;
            }
        return new Note(note, duration, valacity);
    }

    public Chord buildChord(List<String> tokens) {
        System.out.println("inside buildChord");
        tokens = checkForMelodies(tokens);
        Playable[] notes = new Playable[tokens.size()];
        for (int i = 0; i < tokens.size(); i++) {
            if (tokens.get(i).startsWith("[")) {
                notes[i] = buildMelody(tokens.get(i));
                continue;
            }
            notes[i] = buildNote(tokens.get(i));
            System.out.println(tokens.get(i));
        }
        return new Chord(notes);
    }

    public Playable buildMelody(String token) {
        token = token.substring(1, token.length() - 1);
        System.out.println(token);
        StringTokenizer st = new StringTokenizer(token);
        Playable[] notes = new Playable[st.countTokens()];
        for (int i = 0; st.hasMoreTokens(); i++)
            notes[i] = buildNote(st.nextToken());
        return new Melody(notes);
    }

    interface Playable {
        public void play();

        public void setChannelNumber(int channelNumber);
    }

    public class Note implements Playable {
        private int num = 4;

        private int den = 4;

        private int octive = 4;

        private int scale = 0;

        private int shorps = 0;

        private int flats = 0;

        private boolean major = true;

        private int note = 60;

        private int duration = 1000;

        private int valacity = 70;

        private int channelNumber = 0;

        public Note(int note, double duration, int valacity, int cn) {
            this(note, duration, valacity);
            this.channelNumber = cn;
        }

        public Note(int note, double duration, int valacity) {
            this(note, duration);
            this.valacity = valacity;

        }

        public Note(int note, double duration) {
            this.note = note;
            this.duration = (int) (duration * 1000.0);
        }

        public Note(int note) {
            this.note = note;
        }

        public void play() {
            if (note >= 0)
                channels[channelNumber].noteOn(note, valacity);
            try {
                Thread.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (note >= 0)
                channels[channelNumber].noteOff(note, valacity);
        }

        public void setChannelNumber(int channelNumber) {
            this.channelNumber = channelNumber;
        }

        public void setMajor(boolean isMajor) {
            major = isMajor;
        }

    }

    class Melody implements Playable {
        private Playable[] notes;

        private int channelNumber = 0;

        public Melody(Playable[] notes) {
            this.notes = notes;
        }

        public void play() {
            for (int i = 0; i < notes.length; i++)
                notes[i].play();
        }

        public void setChannelNumber(int channelNumber) {
            this.channelNumber = channelNumber;
        }

    }

    class Chord implements Playable {
        private Playable[] notes;

        private int channelNumber = 0;

        public Chord(Playable[] notes) {
            this.notes = notes;
        }

        public void play() {
            for (int i = 1; i < notes.length; i++) {
                final int j = i;
                new Thread(new Runnable() {
                    public void run() {
                        notes[j].setChannelNumber(j);
                        notes[j].play();
                    }
                }).start();
            }
            notes[0].play();
        }

        public void setChannelNumber(int channelNumber) {
            this.channelNumber = channelNumber;
        }

    }

    public static void play() {
        final SingleNoteChannel snc = new SingleNoteChannel();
        final Note[] n = new Note[32];
        final Note[] n1 = new Note[16];
        int[] i = { 1, 3, 5, 6, 1, 6, 5, 3, 1, 3, 5, 6, 8, 10, 12, 13, 13, 12, 10, 8, 13, 8, 10, 12, 13, 12, 10, 8, 6, 5, 3, 1 };// 15, 17,
        // 18, 20,
        // 22, 24,
        // 25};
        for (int j = 0; j < 32; j++)
            n[j] = snc.new Note(i[j] + 60, .5);
        for (int j = 0; j < 32; j += 2)
            n1[j / 2] = snc.new Note(i[j] + 60, 1.0);
        for (int k = 0; k < 100; k++) {
            snc.channels[0].programChange(k);
            new Thread(new Runnable() {
                public void run() {
                    snc.play(n);
                }
            }).start();
            snc.channels[1].programChange(k + 20);
            snc.play(n1);
        }
    }

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

    public void close() {
        for (int i = 0; i < channels.length; i++) {
            channels[i].allNotesOff();
            channels[i].allSoundOff();
        }
        synth.close();
    }

}