package Controller;

import Core.Data.SearchingData;
import Core.Data.TermInfo;
import Core.Operations.QuerySearcher;
import Core.Operations.TextReader;
import Core.Operations.TextWriter;
import Core.Util.TimeStats;
import GraphicUI.SearchingGui;
import gr.uoc.csd.hy463.Topic;
import gr.uoc.csd.hy463.TopicsReader;
import java.io.File;
import java.io.IOException;
import static java.lang.System.exit;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import mitos.stemmer.Stemmer;

/**
 *
 * @author Tasos
 */
public class searchingController {

    private final QuerySearcher searcher;
    private final SearchingData searchingData;
    private String stopwordsPath;
    private static SearchingGui ui;

    private boolean topicLoadCanceled;

    public searchingController() {
        searcher = new QuerySearcher();
        searchingData = searcher.getInputData();
        if (searchingData == null) {
            exit(0);
        }
        Stemmer.Initialize();

        topicLoadCanceled = false;
    }

    private class loadTopic extends SwingWorker<Void, String> {

        private final String path;
        private final String type;

        public loadTopic(String path, String type) {
            this.path = path;
            this.type = type;
        }

        @Override
        protected Void doInBackground() throws Exception {
            TimeStats.startTime_total();

            assert (type.equals("summary") || type.equals("description"));
            searcher.prepareAnalyzeQuery(stopwordsPath);
            ArrayList<Topic> topics = null;
            int lastTopicNumber = 0;
            try {
                final File f = new File(path);
                TextWriter topicsWriter;
                if (f.isFile()) {
                    topics = TopicsReader.readTopics(path);
                    topicsWriter = new TextWriter(f.getParentFile().getAbsolutePath());
                } else {
                    topics = TopicsReader.readTopics(path + TextWriter.getSlashDependsOnSystem() + "topics.xml");
                    topicsWriter = new TextWriter(path);
                }

                for (Topic topic : topics) {
                    if (topicLoadCanceled) {
                        break;
                    }
                    makeSearch(topicsWriter, topic, type);
                    lastTopicNumber = topic.getNumber();
                    publish(TimeStats.timeStatsTopic_toString(lastTopicNumber));
                }
            } catch (Exception ex) {
                Logger.getLogger(searchingController.class.getName()).log(Level.SEVERE, null, ex);
            }

            TimeStats.endTime_total();

            StringBuilder sb = new StringBuilder();
            if (!topicLoadCanceled) {
                sb.append("Topics complete in: ").append(TimeStats.getTotal()).append(" seconds\n");
            } else {
                ui.topicSearchCanceled();
                sb.append("Loading topics canceled\n");
            }
            sb.append("Searching time: ").append(TimeStats.getTotal())
                    .append(" seconds. Total topics: ").append(lastTopicNumber).append("\n");

            publish(sb.toString());
            return null;
        }

        @Override
        protected void done() {
            if (topicLoadCanceled) {
                topicLoadCanceled = false;
                ui.topicSearchCanceled();
            } else {
                ui.topicSearchComplete();
            }

        }

        @Override
        protected void process(final List<String> chunks) {
            chunks.forEach((msg) -> {
                ui.updateLog(msg + "\n");
            });
        }
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
                } else if ("VocabularyFile.txt".equals(fileEntry.getName())) {
                    searchingData.setVocabularyPath(fileEntry.getAbsolutePath());
                } else if ("PostingFile.txt".equals(fileEntry.getName())) {
                    searchingData.setPostingPath(fileEntry.getAbsolutePath());
                } else if ("DocumentsFile.txt".equals(fileEntry.getName())) {
                    searchingData.setDocumentsPath(fileEntry.getAbsolutePath());
                } else if (searchingData.isReady()) {
                    return;
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

        searcher.calculateScore();

        if (!((searcher.getTermsInfo()).isEmpty())) {
            return addResults();
        } else {
            ui.noResults();
            return 0;
        }
    }

    private int addResults() {
        TimeStats.startTime_addResults();
        TermInfo data;
        ArrayList<TermInfo> termsInfo = searcher.getTermsInfo();
        final int size = termsInfo.size();

        final int start = size - 1;
        final int end = Math.max(0, size - 1000); // show first 1000 results, if size > 1000
        TimeStats.endTime_addResults();

        for (int i = start; i >= end; --i) {
            data = termsInfo.get(i);

            TimeStats.startTime_calculateSnippet();
            searcher.preCalculateSnippet(data);
            TimeStats.endTime_calculateSnippet();

            TimeStats.startTime_addResults();
            ui.addResult(size - i, data.getFilePath(), data.getSnippet(), data.getScore());
            TimeStats.endTime_addResults();
        }

        return size;
    }

    public void searchTopics(final String path, final String type) {
        new loadTopic(path, type).execute();
    }

    public void cancelTopicSearch() {
        topicLoadCanceled = true;
    }

    //make searcher for topics
    private void makeSearch(final TextWriter topicsWriter, final Topic topic, final String topicType) {
        TimeStats.reset_timeStatsNotTotal();
        searcher.reset();

        TimeStats.startTime_topic();
        if (topicType.equals("summary")) {
            searcher.analyzeQuery(topic.getSummary() + " " + topic.getType());
        } else {
            searcher.analyzeQuery(topic.getDescription() + " " + topic.getType());
        }

        searcher.calculateScore(); // calculate only score

        if (!((searcher.getTermsInfo()).isEmpty())) {
            writeResults(topicsWriter, topic.getNumber());
        }
        TimeStats.endTime_topic();
    }

    private void writeResults(final TextWriter topicsWriter, final int topicNumber) {
        TimeStats.startTime_writeResults();
        TermInfo data;
        ArrayList<TermInfo> termsInfo = searcher.getTermsInfo();
        final int size = termsInfo.size();
        final int start = size - 1;
        final int end = Math.max(0, size - 1000); // write first 1000 results, if size > 1000

        for (int i = start; i >= end; --i) {
            data = termsInfo.get(i);
            topicsWriter.writeResult(topicNumber, data.calculatePMCID(), size - i, round(data.getScore(), 7));
        }
        TimeStats.endTime_writeResults();
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
