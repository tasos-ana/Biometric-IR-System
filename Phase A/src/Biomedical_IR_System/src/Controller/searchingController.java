package Controller;

import Core.Data.ResultsData;
import Core.Data.SearchingData;
import Core.Operations.QuerySearcher;
import Core.Operations.TextReader;
import Core.Operations.TextWriter;
import GraphicUI.SearchingGui;
import gr.uoc.csd.hy463.Topic;
import gr.uoc.csd.hy463.TopicsReader;
import java.io.File;
import java.io.IOException;
import static java.lang.System.exit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import mitos.stemmer.Stemmer;

public class searchingController {

    private final QuerySearcher searcher;
    private final SearchingData searchingData;
    private ArrayList<ResultsData> resultsData;

    private String stopwordsPath;
    private static SearchingGui ui;

    public searchingController() {
        searcher = new QuerySearcher();
        searchingData = searcher.getInputData();
        if (searchingData == null) {
            exit(0);
        }
        Stemmer.Initialize();
    }

    public boolean loadVocabulary(final String path) throws IOException {
        boolean state = false;
        final File folder = new File(path);
        analyzePath(folder);
        state = searchingData.isReady();
        if (state) {
            final TextReader reader = new TextReader(searchingData.getWords(),
                    searchingData.getVocabularyPath());
            if (reader.isReady()) {
                reader.readVocabulary();
            } else {
                state = false;
                System.err.println("TextReader failed to initialize");
            }
        }
        return state;
    }

    private void analyzePath(final File folder) {
        if ("CollectionIndex".equals(folder.getName())) {
            searchingData.setCollectionRoot(folder.getAbsolutePath());
        }
        if (folder.isDirectory()) {
            for (File fileEntry : folder.listFiles()) {
                if (fileEntry.isDirectory()) {
                    analyzePath(fileEntry);
                } else {
                    if ("VocabularyFile.txt".equals(fileEntry.getName())) {
                        searchingData.setVocabularyPath(fileEntry.getAbsolutePath());
                    } else if ("PostingFile.txt".equals(fileEntry.getName())) {
                        searchingData.setPostingPath(fileEntry.getAbsolutePath());
                    } else if ("DocumentsFile.txt".equals(fileEntry.getName())) {
                        searchingData.setDocumentsPath(fileEntry.getAbsolutePath());
                    } else {
                        if (searchingData.isReady()) {
                            return;
                        }
                    }
                }
            }
        }
    }

    public String getStopwords() {
        return stopwordsPath;
    }

    public void setStopwords(final String stopwords) {
        this.stopwordsPath = stopwords;
    }

    public int makeSearch(String query, final String type) {
        if (!type.contentEquals("None")) {
            query += " " + type;
            System.out.println("search topic: '" + query + "' with type: " + type);
        }
        searcher.reset();
        searcher.prepareAnalyzeQuery(stopwordsPath);
        searcher.analyzeQuery(query);
        searcher.calculateScoreAndSnippet(0);
        resultsData = searcher.getResults();

        if (!resultsData.isEmpty()) {
            return addResults();
        } else {
            ui.noResults();
            return 0;
        }
    }

    private int addResults() {
        ResultsData data;
        final int size = resultsData.size();

        final int start = size - 1;
        final int end = Math.max(0, size - 1000); // show first 1000 results, if size > 1000

        for (int i = start; i >= end; --i) {
            data = resultsData.get(i);
            ui.addResult(size - i, data.getFilePath(), data.getSnippet(), data.getScore());
        }

        return size;
    }

    public int searchTopics(final String topicsPath, final String topicType) {
        assert (topicType.equals("summary") || topicType.equals("description"));
        searcher.prepareAnalyzeQuery(stopwordsPath);
        ArrayList<Topic> topics = null;

        try {
            final File f = new File(topicsPath);
            TextWriter topicsWriter;
            if (f.isFile()) {
                topics = TopicsReader.readTopics(topicsPath);
                topicsWriter = new TextWriter(f.getParentFile().getAbsolutePath());
            } else {
                topics = TopicsReader.readTopics(topicsPath + "\\topics.xml");
                topicsWriter = new TextWriter(topicsPath);
            }
            for (Topic topic : topics) {
                System.out.println("Searching topic: " + topic.getNumber());
                makeSearch(topicsWriter, topic, topicType);
            }
        } catch (Exception ex) {
            Logger.getLogger(searchingController.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (topics == null) { // auto mallon eprepe na einai assetion
            return 0;
        } else {
            return topics.size();
        }
    }

    private void makeSearch(final TextWriter topicsWriter, final Topic topic, final String topicType) {
        searcher.reset();

        if (topicType.equals("summary")) {
            searcher.analyzeQuery(topic.getSummary() + " " + topic.getType());
        } else {
            searcher.analyzeQuery(topic.getDescription() + " " + topic.getType());
        }

        searcher.calculateScoreAndSnippet(1); // calculate only score
        resultsData = searcher.getResults();

        if (!resultsData.isEmpty()) {
            writeResults(topicsWriter, topic.getNumber());
        }

        resultsData = null; // to help garbage collection
    }

    private void writeResults(final TextWriter topicsWriter, final int topicNumber) {
        ResultsData resData;
        final int size = resultsData.size();
        final int start = size - 1;
        final int end = Math.max(0, size - 1000); // write first 1000 results, if size > 1000

        for (int i = start; i >= end; --i) {
            resData = resultsData.get(i);
            topicsWriter.writeResult(topicNumber, resData.getPMCID(), size - i, round(resData.getScore(), 7));
        }
    }

    private double round(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    //****************************************************************************//
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                try {
                    ui = new SearchingGui();
                } catch (IOException ex) {
                    Logger.getLogger(searchingController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }
}
