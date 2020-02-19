package Core.Data;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;

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

        private int freq_tf; // de 3erw pws na to onomasw :)
        private final String tag;
        private final int pos; // mono tin prwti fora tha pairnei to position

        private docsInfo(int f, String t, int p) {
            freq_tf = f;
            tag = t;
            pos = p;
        }
    }

    public void store(final BufferedWriter VocabularyWriter, final RandomAccessFile postingFile,
            final ArrayList<Double> norms, final int numberOfDocs)
            throws IOException {

        // store vocabulary info
        VocabularyWriter.write(Integer.toString(docs.size())); // write df

        // store postings info and calculate norms
        storePostings(postingFile, norms, numberOfDocs);
    }

    // calculate norms while writing posting file
    private void storePostings(final RandomAccessFile file,
            final ArrayList<Double> norms, final int numberOfDocs) {

        // ---------------------------------------------------------------------
        int tf;
        double actualTf;
        final int df;
        final double idf;
        StringBuilder sb;

        df = docs.size();
        idf = Math.log((double) numberOfDocs / df) / Math.log(2.0);
        // =====================================================================

        final Set<Integer> pointersTodoc = docs.keySet();

        try {
            for (int pointer : pointersTodoc) {
                // ---------------------------------------------------------------------
                tf = docs.get(pointer).freq_tf;
                if (tf == -1) {
                    actualTf = 1.0;
                } else {
                    sb = new StringBuilder("0.");
                    sb.append(Integer.toString(tf));
                    actualTf = Double.parseDouble(sb.toString());
                    sb = null; // GC
                }

                norms.add(pointer, norms.get(pointer) + Math.pow(idf * actualTf, 2));
                // =====================================================================

                file.writeInt(pointer + 1); // write doc id
                file.writeInt(docs.get(pointer).freq_tf); // write tf
                file.writeInt(Tags.tag2num(docs.get(pointer).tag));
                file.writeInt(docs.get(pointer).pos); // write position in doc
            }
        } catch (IOException e) {
            System.err.println("Postings write IOException: " + e.getMessage());
        }
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
            docs.get(pointerToDoc).freq_tf++; // increment frequency
        } else {
            docs.put(pointerToDoc, new docsInfo(1, tag, pos
            )); // add pointer
        }

        return this; // get the updated wordInfo
    }

    public int getFreqInDoc(final int pointerToDoc) {
        return docs.get(pointerToDoc).freq_tf;
    }

    // for debug
    public String getWordInfo(final ArrayList<String> documents) {
        StringBuilder info = new StringBuilder();

        Set<String> tags = wordsTags.keySet();
        tags.forEach((tag) -> {
            info.append("<'").append(tag).append("', ")
                    .append(wordsTags.get(tag)).append(">,");
        });

        Set<Integer> pointers = docs.keySet();
        pointers.forEach((pointer) -> {
            info.append(" - [ ").append(documents.get(pointer)).append(" - ").append(docs.get(pointer)).append(" ] ");
        });

        return info.toString();
    }

    public void calculateWordTfs(final ArrayList<Integer> maxFreqs) {
        Integer freq, maxFreq, tf;

        Set<Integer> pointersTodoc = docs.keySet();
        for (Integer pointer : pointersTodoc) {
            freq = docs.get(pointer).freq_tf;
            maxFreq = maxFreqs.get(pointer);

            if (((double) freq / maxFreq) < 1.0) { // ean to tf anikei sto [0,1)
                tf = freq % maxFreq;
            } else { // to tf einai 1
                tf = -1;
            }

            docs.get(pointer).freq_tf = tf;
        }
    }
    
    public void calculateWordNormSum(final ArrayList<Double> norms, final int numberOfDocs) {
        int tf;
        double actualTf;
        final int df;
        final double idf;
        StringBuilder sb;

        df = docs.size();
        idf = Math.log((double) numberOfDocs / df) / Math.log(2.0);

        Set<Integer> pointersTodoc = docs.keySet();
        for (Integer pointer : pointersTodoc) {
            tf = docs.get(pointer).freq_tf;
            if (tf == -1) {
                actualTf = 1.0;
            } else {
                sb = new StringBuilder("0.");
                sb.append(Integer.toString(tf));
                actualTf = Double.parseDouble(sb.toString());
                sb = null; // GC
            }

            norms.add(pointer, norms.get(pointer) + Math.pow(idf * actualTf, 2));
        }
    }
}
