package Core.Operations;

import Core.Data.TermInfo;
import Core.Data.SearchingData;
import Core.Data.Tags;
import Core.Util.Delimiter;
import Core.Util.Stopwords;
import Core.Util.TimeStats;
import gr.uoc.csd.hy463.NXMLFileReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collections;
import java.util.StringTokenizer;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import mitos.stemmer.Stemmer;

/**
 *
 * @author Tasos
 */
public class QuerySearcher {

    private final SearchingData inputData; //vocabulary
    private final ArrayList<TermInfo> termsInfo; //info for each queryWords
    private final TreeMap<String, Integer> queryWords; //<each word from query,freqOnQuery>
    private final TreeMap<Integer, docData> distinctDocs;
    private int numberOfDocs; //Total number of documents
    private String stopwords;

    private class docData {

        String filePath;
        double norm;
    }

    public QuerySearcher() {
        inputData = new SearchingData();
        termsInfo = new ArrayList<>();
        queryWords = new TreeMap<>();
        distinctDocs = new TreeMap<>();
        stopwords = null;
    }

    public SearchingData getInputData() {
        return this.inputData;
    }

    public ArrayList<TermInfo> getTermsInfo() {
        return termsInfo;
    }

    public void prepareAnalyzeQuery(final String stopwordsPath) {
        if (stopwords == null) {
            stopwords = Stopwords.getStopwords(stopwordsPath);
        }
    }

    public void analyzeQuery(String query) {
        TimeStats.startTime_parseQuery();
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
        TimeStats.endTime_parseQuery();

        analyzeTerms();

        TimeStats.startTime_weightsNorms();
        TermInfo.setQueryNorm(Math.sqrt(TermInfo.getQueryNorm()));//Finalize norm
        TimeStats.endTime_weightsNorms();
    }

    private void analyzeTerms() {
        TimeStats.startTime_analyzeTerms();

        numberOfDocs = TextReader.readNumberOfDocuments(inputData.getDocumentsPath());

        TimeStats.startTime_vocabulary();
        queryWords.keySet().forEach((term) -> {
            retrieveVocabularyInfo(term);
        });
        TimeStats.endTime_vocabulary();

        TimeStats.startTime_posting();
        retrievePostingInfo();
        TimeStats.endTime_posting();

        TimeStats.startTime_document();
        retrieveDocumentsInfo();
        TimeStats.endTime_document();

        for (int i = 0; i < termsInfo.size(); ++i) {
            TimeStats.startTime_document();
            //assign data on termsInfo
            TermInfo info = termsInfo.get(i);
            docData d = distinctDocs.get(info.getDocID());
            assert (d.filePath != null && d.norm != -1.0);
            info.setFilePath(d.filePath);
            info.setNorm(d.norm);
            TimeStats.endTime_document();

            analyzeTerm(termsInfo.get(i));
        }
        distinctDocs.clear();

        TimeStats.endTime_analyzeTerms();
    }

    private void retrieveVocabularyInfo(final String term) {
        int termDF = inputData.getDF(term);
        int termPointer = inputData.getPointer(term);
        for (int i = 0; i < termDF; ++i) {
            TermInfo info = new TermInfo();
            info.setTerm(term);
            info.setDf(termDF);
            info.setPointer(termPointer);

            info.setPosition(termPointer - 1 + i); //hold seek position
            termsInfo.add(info);
        }
    }

    private void retrievePostingInfo() {
        try (RandomAccessFile file = new RandomAccessFile(inputData.getPostingPath(), "r")) {
            //to 1o stoixeio exei pointer 1 ara kanoume -1 gia na deiksoume stin arxh tou file
            int rowInBytes = 3 * (Integer.BYTES) + (Double.BYTES);

            long seekAt;
            TermInfo info;
            file.seek(0);
            for (int i = 0; i < termsInfo.size(); ++i) {
                info = termsInfo.get(i);
                seekAt = info.getPosition() * rowInBytes - file.getFilePointer();
                if (seekAt != 0) {
                    file.seek(seekAt);
                }

                info.setDocID(file.readInt());
                info.setTf(file.readDouble());
                info.setTag(Tags.num2tag(file.readInt()));
                //replace seek position with word position in tag
                info.setPosition(file.readInt());
                distinctDocs.put(info.getDocID(), new docData());
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(QuerySearcher.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(QuerySearcher.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void retrieveDocumentsInfo() {
        final File f = new File(inputData.getDocumentsPath());
        String line;
        //retrieve all distict document data from document file
        try (FileReader fr = new FileReader(f);
                BufferedReader reader = new BufferedReader(fr)) {
            reader.readLine(); // 1st line is number of docs
            int cnt = 1;
            while (reader.ready()) {
                line = reader.readLine();
                docData d = distinctDocs.get(cnt);
                if (d != null) {
                    assert (line != null);
                    final String[] arr = line.split(" ");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < (arr.length - 1); ++i) {
                        if (i != 1) {
                            sb.append(" ");
                        }
                        sb.append(arr[i]);
                    }
                    d.filePath = sb.toString();
                    d.norm = Double.parseDouble(arr[arr.length - 1]);
                    distinctDocs.replace(cnt, d);
                }
                cnt++;
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void analyzeTerm(final TermInfo info) {
        TimeStats.startTime_weightsNorms();
        double idf = Math.log((double) numberOfDocs / info.getDf()) / Math.log(2.0);
        info.setDocWeight(info.getTf() * idf);//set doc weight
        double queryTF;
        int maxFreq, termFreq;
        maxFreq = calculateMaxFreq();
        termFreq = queryWords.get(info.getTerm());
        queryTF = (double) termFreq / maxFreq;
        info.setQueryWeight(queryTF * idf);

        TermInfo.increaseQueryNorm(Math.pow(info.getQueryWeight(), 2));
        TimeStats.endTime_weightsNorms();
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

    public void calculateScore() {
        TermInfo info;

        for (int i = 0; i < termsInfo.size(); ++i) {
            info = termsInfo.get(i);
            calculateScore(info);
        }
        sortResults();
    }

    private void calculateScore(final TermInfo info) {
        double numerator, denominator;
        numerator = info.getDocWeight() * info.getQueryWeight();
        denominator = info.getNorm() * TermInfo.getQueryNorm();
        info.setScore(numerator / denominator);
    }

    public void preCalculateSnippet(final TermInfo info) {
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

    private String calculateSnippet(final String text, final int index) {
        String snippet;
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
            } else if (text.charAt(minIndex) == ' ') {
                break;
            } else {
                minIndex--;
            }
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
            } else if (text.charAt(maxIndex) != ' ') {
                maxIndex++;
            } else {
                break;
            }
        }

        snippet = text.substring(minIndex, maxIndex);
        return snippet;
    }

    private void sortResults() {
        Collections.sort(termsInfo, (TermInfo c1, TermInfo c2) -> Double.compare(c1.getScore(), c2.getScore()));
    }

    public void reset() {
        termsInfo.clear();
        queryWords.clear();
    }

}
