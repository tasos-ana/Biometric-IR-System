package Core.Data;

import Core.Util.Delimiter;
import Core.Util.Stopwords;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;
import mitos.stemmer.Stemmer;

/**
 *
 * @author Tasos
 */
public class IndexingData {

    private String stopwords;

    private Vocabulary words;

    private ArrayList<String> documents;
    private ArrayList<Integer> maxFrequencies;
    private ArrayList<Double> norms;

    private final String stopwordsPath;
    private final String collectionPath;
    private final String outputPath;

    public IndexingData(final String stopwordsPath, final String collectionPath, final String outputPath) {
        this.stopwordsPath = stopwordsPath;
        this.collectionPath = collectionPath;
        this.outputPath = outputPath;

        initData();
    }

    private void initData() {
        words = new Vocabulary();

        documents = new ArrayList<>();
        maxFrequencies = new ArrayList<>();
        norms = new ArrayList<>();

        initStopWords();
    }

    private void initStopWords() {
        assert (stopwordsPath != null);
        stopwords = Stopwords.getStopwords(stopwordsPath);
        assert (stopwords != null);
    }

    public String getStopwords() {
        return stopwords;
    }

    public Vocabulary getWords() {
        return words;
    }

    public ArrayList<String> getDocuments() {
        return documents;
    }

    public ArrayList<Integer> getMaxFrequencies() {
        return maxFrequencies;
    }

    public ArrayList<Double> getNorms() {
        return norms;
    }

    public String getStopwordsPath() {
        return stopwordsPath;
    }

    public String getCollectionPath() {
        return collectionPath;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void updateWords(final String tagName, final String tagWords, final int pointerToDoc) {
        final StringTokenizer tokenizer = new StringTokenizer(tagWords, Delimiter.getDelimeters());

        while (tokenizer.hasMoreTokens()) {
            String currentToken = tokenizer.nextToken();
            String token = currentToken;
            currentToken = currentToken.toLowerCase();

            if (!stopwords.contains(currentToken)) { // exclude stopwords
                int index = tagWords.indexOf(token);
                token = null;
                currentToken = Stemmer.Stem(currentToken); // do the stemming

                final int freqOfTokenInDoc = words.addWord(currentToken, tagName, pointerToDoc, index);

                if (freqOfTokenInDoc > maxFrequencies.get(pointerToDoc)) {
                    maxFrequencies.set(pointerToDoc, freqOfTokenInDoc); // update max freq
                }
            }
        }
    }

    public void updateWords(final String tagName, final ArrayList<String> tagWords, final int pointerToDoc) {
        tagWords.forEach((str) -> {
            updateWords(tagName, str, pointerToDoc);
        });
    }

    public void updateWords(final String tagName, final HashSet<String> tagWords, final int pointerToDoc) {
        tagWords.forEach((str) -> {
            updateWords(tagName, str, pointerToDoc);
        });
    }

    public void storeDocuments(final BufferedWriter writer) throws IOException {
        writer.write(Integer.toString(documents.size()));
        writer.newLine();

        for (int i = 0; i < documents.size(); ++i) {
            writer.write(Integer.toString(i + 1)); // write doc id
            writer.write(" ");
            writer.write(documents.get(i)); // write doc path
            writer.write(" ");
            writer.write(Double.toString(Math.sqrt(norms.get(i)))); // write square root of norm sum
            writer.newLine();
        }
    }
}
