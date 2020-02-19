package Core.Data;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

/**
 *
 * @author Tasos
 */
public class Vocabulary {

    private final TreeMap<String, Occurrences> wordsInfo;
    private double wordBytesSum;

    public Vocabulary() {
        wordsInfo = new TreeMap<>();
        wordBytesSum = 0;
    }

    //SEARCHING 
    public void loadVocabulary(final BufferedReader reader) throws IOException {
        String lineData;
        String[] lineDataArray;
        String term;
        int df, pointer;
        while (reader.ready()) {
            lineData = reader.readLine();
            lineDataArray = lineData.split(" ");
            term = lineDataArray[0];
            df = Integer.parseInt(lineDataArray[1]);
            pointer = Integer.parseInt(lineDataArray[2]);
            wordsInfo.put(term, new Occurrences(pointer, df));
        }
    }

    Occurrences getValue(final String query) {
        return wordsInfo.get(query);
    }

    //INDEXING
    public String store(final BufferedWriter vocabularyWriter, final BufferedOutputStream postingFile,
            final ArrayList<Integer> maxFrequencies, final ArrayList<Double> norms, final int numberOfDocs)
            throws IOException {

        int pointer = 1; // = line in postings file
        StringBuilder sb = new StringBuilder();
//        try {
//            postingFile.seek(0); // start from beginning of file
//        } catch (IOException e) {
//            System.err.println("Postings seek IOException: " + e.getMessage());
//        }

        Iterator it = wordsInfo.keySet().iterator();
        long tStart, tEnd;
        long tVoc = 0, tTFsNorms = 0, tPosting = 0, retPostingTime;
        while (it.hasNext()) {
            String word = (String) it.next();
            Occurrences oc = wordsInfo.get(word);

            //======= START - Store Vocabulary =======
            tStart = System.currentTimeMillis();

            vocabularyWriter.write(word); // write word
            vocabularyWriter.write(" ");
            vocabularyWriter.write(Integer.toString(oc.getDf())); // write df
            vocabularyWriter.write(" ");
            vocabularyWriter.write(Integer.toString(pointer));
            vocabularyWriter.newLine();

            tEnd = System.currentTimeMillis();
            tVoc += tEnd - tStart;
            //======= END - Store Vocabulary =======

            // ======= START - Calculate TFs & Norms -> Store Postings =======
            tStart = System.currentTimeMillis();
            retPostingTime = oc.calculateTfsAndNormsAndsStorePostings(postingFile,
                    maxFrequencies, norms, numberOfDocs);

            tEnd = System.currentTimeMillis();
            tPosting += retPostingTime;
            tTFsNorms += (tEnd - tStart) - retPostingTime;
            //======= END - Calculate TFs & Norms -> Store Postings =======

            pointer += wordsInfo.get(word).getDf();

            it.remove();
            assert (wordsInfo.get(word) == null);
        }
        sb.append("Vocabulary created in: ").append(tVoc / 1000.0).append(" seconds\n");
        sb.append("TFs & Norms calculated in: ").append(tTFsNorms / 1000.0).append(" seconds\n");
        sb.append("Posting created in: ").append(tPosting / 1000.0).append(" seconds\n");

        return sb.toString();
    }

    public int addWord(final String word, final String tag, final int pointerToDoc, final int positionInDoc) {
        int freq = 1;

        if (wordsInfo.containsKey(word)) { // if already has that word
            final Occurrences wordInfo = wordsInfo.get(word).SetAndGetInfo(tag, pointerToDoc, positionInDoc);
            wordsInfo.put(word, wordInfo);
            freq = wordInfo.getFreqInDoc(pointerToDoc);
        } else {
            wordsInfo.put(word, new Occurrences(tag, pointerToDoc, positionInDoc));
        }

        return freq;
    }

    public String getInfo(final ArrayList<String> documents) {
        final StringBuilder info = new StringBuilder();

        final Set<String> words = wordsInfo.keySet();
        words.forEach((word) -> {
            wordBytesSum += word.length();
        });

        final int wordsNum = wordsInfo.size();
        info.append("Distinct Words: ").append(wordsNum).append("\n");
        info.append("in Bytes: ").append(wordBytesSum).append("\n");
        if (wordsNum != 0) {
            info.append("Bytes per word: ").append(wordBytesSum / wordsNum);
        }

        return info.toString();
    }
}
