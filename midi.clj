(import '(javax.sound.midi MidiChannel MidiSystem MidiUnavailableException Synthesizer))

(def ^Synthesizer synthesizer (. MidiSystem getSynthesizer))

(. synthesizer open)

(def channels (.getChannels synthesizer ))

(def channel (first channels))

(defn play-single-note [note valacity duration]
  (. channel noteOn note valacity)
  (Thread/sleep duration)
  (. channel noteOff note valacity))

(def notes (hash-map \c 0 \d 2 \e 4 \f 5 \g 7 \a 9 \b 11))

(def valacity 70)

(def duration 1000)

(def middle-c 60)

(defn play [s]
  (if (= (.length s) 0)
    nil
    (let [fc (first s)]
      (play-single-note (+ middle-c (notes fc)) valacity duration)))
  (if (= (.length s) 0)
    nil
    (play (subs s 1))))

(defn read-and-play [message]
  (println message)
  (let [s (read-line)]
    (play s))
  (read-and-play "Enter some more notes:"))

(read-and-play "Enter some notes to play:")
