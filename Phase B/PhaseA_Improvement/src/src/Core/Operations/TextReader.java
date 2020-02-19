package Core.Operations;

import Core.Data.Vocabulary;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Tasos
 */
public class TextReader {

    private final Vocabulary voc;
    private final File vocabularyFile;

    public TextReader(final Vocabulary voc, final String path) {
        this.voc = voc;
        this.vocabularyFile = initFile(path);
    }

    private File initFile(final String path) {
        File tmp = new File(path);

        if (!tmp.exists()) {
            tmp = null;
        }

        return tmp;
    }

    private boolean isValidFile(final File f) {
        return f.exists();
    }

    public boolean isReady() {
        boolean state = true;
        if (!isValidFile(vocabularyFile) || voc == null) {
            state = false;
        }
        return state;
    }

    public void readVocabulary() throws FileNotFoundException, IOException {
        try (FileReader fr = new FileReader(vocabularyFile);
                final BufferedReader reader = new BufferedReader(fr)) {
            voc.loadVocabulary(reader);
        }
    }

    public static String readFile(final String path) throws FileNotFoundException, IOException {
        final StringBuilder sb = new StringBuilder();
        final File f = new File(path);
        if (!f.exists()) {
            sb.append("File not exists");
        } else {
            try (FileReader fr = new FileReader(f);
                    final BufferedReader reader = new BufferedReader(fr)) {
                while (reader.ready()) {
                    sb.append(reader.readLine());
                }
            }
        }
        return sb.toString();
    }

    public static int readNumberOfDocuments(final String documentsPath) {
        final File f = new File(documentsPath);
        int numberOfDocs = -1;
        try {
            final FileReader fr = new FileReader(f);
            final BufferedReader reader = new BufferedReader(fr);

            numberOfDocs = Integer.parseInt(reader.readLine()); // 1st line is number of docs

            reader.close();
            fr.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextReader.class.getName()).log(Level.SEVERE, null, ex);
        }

        return numberOfDocs;
    }

    public static String readDocumentInfo(final int documentsPointer, final String documentsPath) {
        String retval = null;
        final File f = new File(documentsPath);
        try {
            final FileReader fr = new FileReader(f);
            final BufferedReader reader = new BufferedReader(fr);

            reader.readLine(); // 1st line is number of docs

            int cnt = 1;
            while (reader.ready()) {
                retval = reader.readLine();
                if (cnt == documentsPointer) {
                    break;
                }
                cnt++;
            }

            reader.close();
            fr.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextReader.class.getName()).log(Level.SEVERE, null, ex);
        }

        return (retval.split(" "))[1];
    }
}
