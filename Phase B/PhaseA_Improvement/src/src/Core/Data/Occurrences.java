package Core.Data;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author Tasos
 */
public class Occurrences {

    private final int[] info; // [0] = df, [1]=pointer

    private final TreeMap<String, Integer> wordsTags; // <tag, occurrences>
    private final HashMap<Integer, docsInfo> docs; // <docPointer, (freq_tf, position)>
    // gia kathe doc, krataei to freq autou tou term se auto to doc

    //SEARCHING - Constructor to read Voc data for query searching
    public Occurrences(final int docPointer, final int df) {
        info = new int[2];
        info[0] = df;
        info[1] = docPointer;
        wordsTags = null;
        docs = null;
    }

    //INDEXING - Constructor to store Voc data for indexing 
    public Occurrences(final String firstTagFound, final int pointerToDoc, final int pos) {
        wordsTags = new TreeMap<>();
        wordsTags.put(firstTagFound, 1);

        docs = new HashMap<>();
        docs.put(pointerToDoc, new docsInfo(1, firstTagFound, pos));
        info = null;
    }

    private class docsInfo {

        private double freq; // de 3erw pws na to onomasw :)
        private final String tag;
        private final int pos; // mono tin prwti fora tha pairnei to position

        private docsInfo(int f, String t, int p) {
            freq = f;
            tag = t;
            pos = p;
        }
    }

    public long calculateTfsAndNormsAndsStorePostings(
            final BufferedOutputStream file,
            //            final RandomAccessFile file,
            final ArrayList<Integer> maxFreqs, final ArrayList<Double> norms,
            final int numberOfDocs) {

        long tStart, tEnd;
        long tPosting = 0;

        //Required for TF,norms
        int maxFreq;
        final int df;
        final double idf;
        double tf, freq;

        df = docs.size();
        idf = Math.log((double) numberOfDocs / df) / Math.log(2.0);
        //========================================================

        //------------------------------------
        Iterator it = docs.keySet().iterator();
        try {
            while (it.hasNext()) {
                Integer pointer = (Integer) it.next();

                // ======= START Calculate TF =======
                double weight = 0.0;
                if (wordsTags.containsKey("PMC ID")) {
                    weight += 20.0;
                }
                if (wordsTags.containsKey("Title")) {
                    weight += 12.0;
                }
                if (wordsTags.containsKey("Categories")) {
                    weight += 8.0;
                }

                freq = docs.get(pointer).freq;
                maxFreq = maxFreqs.get(pointer);
                tf = freq / maxFreq;

                tf = tf + tf * (weight / 100);
                if (tf > 1.0) {
                    tf = 1.0;
                }
                // ======= END Calculate TF =======

                // ======= START Calculate Norm =======
                double val = norms.get(pointer);
                val += Math.pow(idf * tf, 2);
                norms.set(pointer, val);
                // ======= END Calculate Norm =======

                // ======= START Store Posting =======
                tStart = System.currentTimeMillis();

                //_____________________________________________--
                ByteBuffer b1 = ByteBuffer.allocate(Integer.BYTES);
                ByteBuffer b2 = ByteBuffer.allocate(Double.BYTES);
                ByteBuffer b3 = ByteBuffer.allocate(Integer.BYTES);
                ByteBuffer b4 = ByteBuffer.allocate(Integer.BYTES);

                IntBuffer intBuffer1 = b1.asIntBuffer();
                intBuffer1.put(pointer + 1);

                DoubleBuffer doubleBuffer2 = b2.asDoubleBuffer();
                doubleBuffer2.put(tf);

                IntBuffer intBuffer3 = b3.asIntBuffer();
                intBuffer3.put(Tags.tag2num(docs.get(pointer).tag));

                IntBuffer intBuffer4 = b4.asIntBuffer();
                intBuffer4.put(docs.get(pointer).pos);

                file.write(b1.array());
                file.write(b2.array());
                file.write(b3.array());
                file.write(b4.array());
                //---------------------------------------------------

                tEnd = System.currentTimeMillis();
                tPosting += tEnd - tStart;
                // ======= END Store Posting =======
            }
        } catch (IOException e) {
            System.err.println("Postings write IOException: " + e.getMessage());
        }
        return tPosting;
    }

    public int getDf() {
        if (docs == null) {
            assert (info != null);
            return info[0];
        } else {
            return docs.size();
        }
    }

    public int getDocPointer() {
        if (info != null) {
            return info[1];
        } else {
            return -1;
        }
    }

    public Occurrences SetAndGetInfo(final String tag, final int pointerToDoc, final int pos) {
        // add tag
        Integer value;
        if ((value = wordsTags.get(tag)) != null) { // if already has that tag
            wordsTags.put(tag, ++value); // increment value
        } else {
            wordsTags.put(tag, 1); // add tag
        }

        // add frequency and position in document
        if (docs.get(pointerToDoc) != null) { // if already has that document
            docs.get(pointerToDoc).freq += 1.0; // increment frequency
        } else {
            docs.put(pointerToDoc, new docsInfo(1, tag, pos
            )); // add pointer
        }

        return this; // get the updated wordInfo
    }

    public int getFreqInDoc(final int pointerToDoc) {
        return (int) docs.get(pointerToDoc).freq;
    }

    // for DEBUG
    public String getWordInfo(final ArrayList<String> documents) {
        StringBuilder wordsInfo = new StringBuilder();

        Set<String> tags = wordsTags.keySet();
        tags.forEach((tag) -> {
            wordsInfo.append("<'").append(tag).append("', ")
                    .append(wordsTags.get(tag)).append(">,");
        });

        Set<Integer> pointers = docs.keySet();
        pointers.forEach((pointer) -> {
            wordsInfo.append(" - [ ").append(documents.get(pointer)).append(" - ").append(docs.get(pointer)).append(" ] ");
        });

        return wordsInfo.toString();
    }
}
