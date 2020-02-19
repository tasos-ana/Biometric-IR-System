package Core.Operations;

import Core.Data.IndexingData;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextWriter {

    private final IndexingData data;
    private final File vocabularyFile;
    private final File postingFile;
    private final File documentsFile;

    private final File resultsFile;

    public TextWriter(final String resRoot) {
        resultsFile = prepareFiles(resRoot, "results.txt");

        this.data = null;
        vocabularyFile = null;
        postingFile = null;
        documentsFile = null;
    }

    public TextWriter(final IndexingData data) {
        this.data = data;
        final String output = data.getOutputPath();
        vocabularyFile = prepareFiles(output, "VocabularyFile.txt");
        postingFile = prepareFiles(output, "PostingFile.txt");
        documentsFile = prepareFiles(output, "DocumentsFile.txt");

        resultsFile = null;
    }

    private File prepareFiles(final String rootPath, final String targetFile) {
        final File dir = new File(rootPath);
        String errorMessage;
        try {
            if (!dir.exists()) {
                try {
                    dir.mkdir();
                } catch (SecurityException se) {
                    System.out.println("In preStore when folder dir creating: ");
                    errorMessage = se.getMessage();
                    System.out.println(errorMessage);
                }
            }
        } catch (SecurityException se) {
            System.out.println("In preStore when folder exist checking: ");
            errorMessage = se.getMessage();
            System.out.println(errorMessage);
        }

        final File f = new File(rootPath + getSlashDependsOnSystem() + targetFile);

        try {
            if (!f.exists()) {
                try {
                    f.createNewFile();
                } catch (IOException se) {
                    System.out.println("In preStore when file creating: ");
                    errorMessage = se.getMessage();
                    System.out.println(errorMessage);
                }
            } else {
                f.delete();
                try {
                    f.createNewFile();
                } catch (IOException se) {
                    System.out.println("In preStore when file creating: ");
                    errorMessage = se.getMessage();
                    System.out.println(errorMessage);
                }
            }
        } catch (SecurityException se) {
            System.out.println("In preStore when file exist checking: ");
            errorMessage = se.getMessage();
            System.out.println(errorMessage);
        }

        return f;
    }

    public static String getSlashDependsOnSystem() {
        String slash;
        String OS = System.getProperty("os.name").toLowerCase();
        if ((OS.contains("win"))) {
            slash = "\\";
        } else {
            slash = "/";
        }
        return slash;
    }

    public String store() throws IOException {
        final FileWriter fw = new FileWriter(vocabularyFile);
        final BufferedWriter vocabularyWriter = new BufferedWriter(fw);
        String stats = " ";

        // calculate norms while writing posting file and vocabulary
        try (BufferedOutputStream file = new BufferedOutputStream(new FileOutputStream(postingFile))) {
            // calculate norms while writing posting file and vocabulary
            stats = data.getWords().store(vocabularyWriter, file,
                    data.getMaxFrequencies(), data.getNorms(), data.getDocuments().size());
        } catch (FileNotFoundException e) {
            System.err.println("Postings FileNotFoundException: " + e.getMessage());
        } finally {
            vocabularyWriter.close();
            fw.close();
        }
        return stats;
    }

    public String storeDocuments() throws IOException {
        final FileWriter fw = new FileWriter(documentsFile);
        final BufferedWriter writer = new BufferedWriter(fw);
        final long tStart = System.currentTimeMillis();

        data.storeDocuments(writer);

        writer.close();
        fw.close();
        final long tEnd = System.currentTimeMillis();
        final long tDelta = tEnd - tStart;
        final double elapsedSeconds = tDelta / 1000.0;

        return "Document File created in: \t" + elapsedSeconds + " seconds";
    }

    public void writeResult(final int topicNo, final String pmcid, final int rank, final double score) {
        try (final FileWriter fw = new FileWriter(resultsFile, true); // append = true
                final BufferedWriter writer = new BufferedWriter(fw)) {

            writer.write(Integer.toString(topicNo));
            writer.write(" 0 "); // Q0
            writer.write(pmcid);
            writer.write(" ");
            writer.write(Integer.toString(rank));
            writer.write(" ");
            writer.write(Double.toString(score));
            writer.write(" IR_system_1\n"); // RUN_NAME
        } catch (IOException ex) {
            Logger.getLogger(TextWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
