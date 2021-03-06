package Controller;

import Core.Data.IndexingData;
import Core.Operations.TextAnalyzer;
import Core.Operations.TextWriter;
import GraphicUI.IndexingGui;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class indexingController extends SwingWorker<Void, String> {

    private IndexingData indexingData;

    private String collection;

    private static IndexingGui ui;
    private TextAnalyzer analyzer;
    private TextWriter writter;

    public indexingController() {
        collection = null;
    }

    @Override
    protected Void doInBackground() throws Exception {
        publish("\n\n");
        final long tStart = System.currentTimeMillis();
        startIndexing();
        startWriting();
        final long tEnd = System.currentTimeMillis();
        publish("\nTotal run time: " + (tEnd - tStart) / 1000.0 + " seconds");
        return null;
    }

    @Override
    protected void done() {
        complete();
    }

    @Override
    protected void process(final List<String> chunks) {
        chunks.forEach((msg) -> {
            ui.updateLog(msg + "\n");
        });
    }

    public void updateCollection(final String path) {
        this.collection = path;
    }

    public boolean isReady() {
        return collection != null;
    }

    public void startIndexing() {
        try {
            publish("Indexing start...");
            initIndexing();
            publish(analyzer.analyzeCollection());
            publish("Indexing complete...\n");
        } catch (IOException ex) {
            Logger.getLogger(indexingController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initIndexing() {
        indexingData = new IndexingData("5_Resources_Stoplists", collection, "CollectionIndex");
        analyzer = new TextAnalyzer(indexingData);
    }

    private void startWriting() {
        try {
            publish("Store vocabulary, calculate tf & norms, store postings start...");
            initWriter();
            publish(writter.store());

            publish(writter.storeDocuments());
        } catch (IOException ex) {
            System.err.println("Indexing write documents IOException: " + ex.getMessage());
        }
    }

    private void initWriter() {
        writter = new TextWriter(indexingData);
    }

    private void complete() {
        ui.complete();
        indexingData = null;
        collection = null;
        analyzer = null;
        writter = null;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                ui = new IndexingGui();
            }
        });
    }
}
