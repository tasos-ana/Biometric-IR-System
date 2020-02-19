package Core.Data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeMap;

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
    public void store(final BufferedWriter vocabularyWriter, final RandomAccessFile postingFile,
            final ArrayList<Double> norms, final int numberOfDocs)
            throws IOException {

        int pointer = 1; // = line in postings file

        try {
            postingFile.seek(0); // start from beginning of file
        } catch (IOException e) {
            System.err.println("Postings seek IOException: " + e.getMessage());
        }

        final Set<String> words = wordsInfo.keySet();
        for (String word : words) {
            vocabularyWriter.write(word); // write word
            vocabularyWriter.write(" ");

            // calculate norms while writing posting file and vocabulary
            wordsInfo.get(word).store(vocabularyWriter, postingFile, norms, numberOfDocs);
            vocabularyWriter.write(" ");
            vocabularyWriter.write(Integer.toString(pointer));
            vocabularyWriter.newLine();

            pointer += wordsInfo.get(word).getDf();
        }
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
            info.append("Bytes per word: ").append(wordBytesSum / wordsNum).append("\n");
        }

        return info.toString();
    }

    public void calculateAllTfs(final ArrayList<Integer> maxFreqs) {
        final Set<String> words = wordsInfo.keySet();
        words.forEach((word) -> {
            wordsInfo.get(word).calculateWordTfs(maxFreqs);
        });
    }

    public void calculateNormSum(final ArrayList<Double> norms, final int numberOfDocs) {
        final Set<String> words = wordsInfo.keySet();
        words.forEach((word) -> {
            wordsInfo.get(word).calculateWordNormSum(norms, numberOfDocs);
        });
    }
}
