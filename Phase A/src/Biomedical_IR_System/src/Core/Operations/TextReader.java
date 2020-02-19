package Core.Operations;

import Core.Data.Vocabulary;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    public static ArrayList<Integer> readTermPosting(final int start, final int df, final String path) {
        final ArrayList<Integer> retVal = new ArrayList<>();
        try {
            final RandomAccessFile file = new RandomAccessFile(path, "r");
            //to 1o stoixeio exei pointer 1 ara kanoum -1 gia na deiksoume stin arxh tou file
            file.seek((start - 1) * 12);
            for (int i = 0; i < df; ++i) {
                retVal.add(file.readInt()); // add doc#
                retVal.add(file.readInt()); // add tf
                int pos = file.readInt();
            }
            file.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextReader.class.getName()).log(Level.SEVERE, null, ex);
        }

        return retVal;
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
