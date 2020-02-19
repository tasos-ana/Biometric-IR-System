package Core.Operations;

import Core.Data.IndexingData;
import gr.uoc.csd.hy463.NXMLFileReader;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import mitos.stemmer.Stemmer;

public class TextAnalyzer {

    private final IndexingData data;

    public TextAnalyzer(final IndexingData data) {
        Stemmer.Initialize();
        this.data = data;
    }

    public String analyzeCollection()
            throws UnsupportedEncodingException, IOException {

        final File folder = new File(data.getCollectionPath());
        assert (folder.isDirectory());

        final long tStart = System.currentTimeMillis();

        analyzeFilesInFolder(folder);
        System.out.println("Analyzed folder: " + folder.getName());

        //data.calculateTfsAndNorms_threads();//With threads
        final long tEnd = System.currentTimeMillis();
        final long tDelta = tEnd - tStart;
        final double elapsedSeconds = tDelta / 1000.0;

        final String info = data.getWords().getInfo(data.getDocuments());

        return "Collection analyzing complete in: " + elapsedSeconds + " seconds \n" + info;
    }

    private void analyzeFilesInFolder(final File folder) throws IOException {
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                analyzeFilesInFolder(fileEntry);
                System.out.println("Analyzed folder: " + fileEntry.getName());
            } else {
                analyzeFile(fileEntry.getAbsolutePath());
            }
        }
    }

    private void analyzeFile(final String filePath) throws IOException {
        final File file = new File(filePath);
        assert (file.isFile());
        final ArrayList<String> documents = data.getDocuments();
        final ArrayList<Integer> maxFrequencies = data.getMaxFrequencies();
        final ArrayList<Double> norms = data.getNorms();

        documents.add(filePath);
        maxFrequencies.add(0);
        norms.add(0.0);

        final int lastDocIndex = documents.size() - 1;

        final NXMLFileReader xmlFile = new NXMLFileReader(file);

        // get words of all tags
        data.updateWords("PMC ID", xmlFile.getPMCID(), lastDocIndex);
        data.updateWords("Title", xmlFile.getTitle(), lastDocIndex);
        data.updateWords("Abstract", xmlFile.getAbstr(), lastDocIndex);
        data.updateWords("Body", xmlFile.getBody(), lastDocIndex);
        data.updateWords("Journal", xmlFile.getJournal(), lastDocIndex);
        data.updateWords("Publisher", xmlFile.getPublisher(), lastDocIndex);
        data.updateWords("Authors", xmlFile.getAuthors(), lastDocIndex);
        data.updateWords("Categories", xmlFile.getCategories(), lastDocIndex);
    }
}
