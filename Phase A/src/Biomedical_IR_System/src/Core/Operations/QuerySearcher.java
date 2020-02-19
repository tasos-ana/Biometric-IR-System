package Core.Operations;

import Core.Data.ResultsData;
import Core.Data.TermInfo;
import Core.Data.SearchingData;
import Core.Data.Tags;
import Core.Util.Delimiter;
import Core.Util.Stopwords;
import gr.uoc.csd.hy463.NXMLFileReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import mitos.stemmer.Stemmer;

public class QuerySearcher {

    private final SearchingData inputData; //vocabulary
    private final ArrayList<TermInfo> termsInfo; //info for each queryWords
    private final TreeMap<String, Integer> queryWords; //<each word from query,freqOnQuery>
    private int numberOfDocs; //Total number of documents
    private String stopwords;

    public QuerySearcher() {
        inputData = new SearchingData();
        termsInfo = new ArrayList<>();
        queryWords = new TreeMap<>();
    }

    public SearchingData getInputData() {
        return this.inputData;
    }

    public void prepareAnalyzeQuery(final String stopwordsPath) {
        stopwords = Stopwords.getStopwords(stopwordsPath);
    }

    public void analyzeQuery(String query) {
        int value;
        query = query.toLowerCase();
        final StringTokenizer tokenizer = new StringTokenizer(query, Delimiter.getDelimeters());
        while (tokenizer.hasMoreTokens()) {
            String currentToken = tokenizer.nextToken();
            currentToken = currentToken.toLowerCase();
            if (!stopwords.contains(currentToken)) { // exclude stopwords
                currentToken = Stemmer.Stem(currentToken); // do the stemming
                if (queryWords.containsKey(currentToken)) {
                    value = queryWords.get(currentToken);
                    queryWords.put(currentToken, value++);
                } else {
                    queryWords.put(currentToken, 1);
                }
            }
        }
        analyzeTerms();
        TermInfo.setQueryNorm(Math.sqrt(TermInfo.getQueryNorm()));//Finalize norm
    }

    private void analyzeTerms() {
        numberOfDocs = TextReader.readNumberOfDocuments(inputData.getDocumentsPath());
        for (String term : queryWords.keySet()) {
            retrieveTermInfo(term);
        }
        for (int i = 0; i < termsInfo.size(); ++i) {
            analyzeTerm(termsInfo.get(i));
        }
    }

    private void retrieveTermInfo(final String term) {
        int termDF = inputData.getDF(term);
        int termPointer = inputData.getPointer(term);
        for (int i = 0; i < termDF; ++i) {
            TermInfo info = new TermInfo();
            info.setTerm(term);
            info.setDf(termDF);
            info.setPointer(termPointer);
            info.retrievePostingInfo(inputData.getPostingPath(), i);
            info.retrieveDocumentsInfo(inputData.getDocumentsPath());
            termsInfo.add(info);
        }
    }

    private void analyzeTerm(final TermInfo info) {
        double idf = Math.log((double) numberOfDocs / info.getDf()) / Math.log(2.0);
        info.setDocWeight(info.getTf() * idf);//set doc weight
        double queryTF;
        int maxFreq, termFreq;
        maxFreq = calculateMaxFreq();
        termFreq = queryWords.get(info.getTerm());
        queryTF = (double) termFreq / maxFreq;
        info.setQueryWeight(queryTF * idf);

        TermInfo.increaseQueryNorm(Math.pow(info.getQueryWeight(), 2));
    }

    private int calculateMaxFreq() {
        int max = -1;
        for (Integer value : queryWords.values()) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    public void calculateScoreAndSnippet(final int onlyScore) {
        TermInfo info;
        for (int i = 0; i < termsInfo.size(); ++i) {
            info = termsInfo.get(i);
            calculateScore(info);
            if (onlyScore == 0) {
                preCalculateSnippet(info);
            } else {
                calculatePMCID(info);
            }
        }
    }

    private void calculateScore(final TermInfo info) {
        double numerator, denominator;
        numerator = info.getDocWeight() * info.getQueryWeight();
        denominator = info.getNorm() * TermInfo.getQueryNorm();
        info.setScore(numerator / denominator);
    }

    private void preCalculateSnippet(final TermInfo info) {
        final File file = new File(info.getFilePath());
        final String tag = info.getTag();
        String snippet = null;
        final int pos = info.getPosition();
        try {
            final NXMLFileReader xmlFile = new NXMLFileReader(file);
            switch (tag) {
                case "Authors":
                    for (String fileText : xmlFile.getAuthors()) {
                        if (fileText.contains(info.getTerm())) {
                            snippet = calculateSnippet(fileText, pos);
                            if (snippet != null) {
                                break;
                            }
                        }
                    }
                    break;
                case "Categories":
                    for (String fileText : xmlFile.getCategories()) {
                        snippet = calculateSnippet(fileText, pos);
                        if (fileText.contains(info.getTerm())) {
                            snippet = calculateSnippet(fileText, pos);
                            if (snippet != null) {
                                break;
                            }
                        }
                    }
                    break;
                default:
                    final String fileText = Tags.getText(xmlFile, tag);
                    snippet = calculateSnippet(fileText, pos);
                    assert (snippet != null);
                    break;
            }
            assert (snippet != null);
            info.setSnippet(snippet);
        } catch (IOException ex) {
            Logger.getLogger(QuerySearcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void calculatePMCID(final TermInfo info) {
        try {
            final File file = new File(info.getFilePath());
            final NXMLFileReader xmlFile = new NXMLFileReader(file);
            info.setPMCID(xmlFile.getPMCID()); // ALERT den tha benei edw sta topics
        } catch (IOException ex) {
            Logger.getLogger(QuerySearcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private String calculateSnippet(final String text, final int index) {
        String snippet = null;
        final int len = text.length();
        int minIndex = index - 35;
        int maxIndex = index + 35;

        //calculate minimun Index
        while (true) {
            if (minIndex < 0) {
                maxIndex += (-1) * minIndex;
                minIndex = 0;
                if (maxIndex > len) {
                    return text;//giati to default snippet size>= text size
                }
                break;
            } else {
                if (text.charAt(minIndex) == ' ') {
                    break;
                } else {
                    minIndex--;
                }
            }
            //System.out.println("min");
        }

        //calculate maximum Index
        while (true) {
            if (maxIndex >= len) {
                if (minIndex == 0) {
                    return text;
                } else {
                    maxIndex = len - 1;
                    break;
                }
            } else {
                if (text.charAt(maxIndex) != ' ') {
                    maxIndex++;
                } else {
                    break;
                }
            }
            //System.out.println("min");
        }

        snippet = text.substring(minIndex, maxIndex);
        return snippet;
    }

    public ArrayList<ResultsData> getResults() {
        final ArrayList<ResultsData> res = new ArrayList<>();
        prepareResults(res);
        sortResults(res);
        return res;
    }

    private void prepareResults(final ArrayList<ResultsData> res) {
        assert (res != null);
        String path, snippet;
        double score;
        TermInfo info;
        for (int i = 0; i < termsInfo.size(); i++) {
            info = termsInfo.get(i);
            path = info.getFilePath();
            snippet = info.getSnippet();
            score = info.getScore();
            res.add(new ResultsData(path, snippet, score, info.getPMCID()));
        }
    }

    private void sortResults(final ArrayList<ResultsData> res) {
        Collections.sort(res, (ResultsData c1, ResultsData c2) -> Double.compare(c1.getScore(), c2.getScore()));
    }

    public void reset() {
        termsInfo.clear();
        queryWords.clear();
    }

}
