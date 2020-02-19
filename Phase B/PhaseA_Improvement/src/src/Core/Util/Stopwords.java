package Core.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Stopwords {

    public static boolean isValid(final String filePath) {
        final File file = new File(filePath);
        return file.exists();
    }

    public static String getStopwords(final String filePath) {
        final File file = new File(filePath);
        String allStopwords = null;
        if (file.isDirectory()) {
            allStopwords = getStopwordsFromFolder(file); // read files from folder recursively
        } else {
            allStopwords = getStopwordsFromFile(file);
        }

        return allStopwords;
    }

    private static String getStopwordsFromFolder(final File folder) {
        assert (folder.isDirectory());

        final StringBuilder stopwords = new StringBuilder();
        for (File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                final String res = getStopwordsFromFolder(fileEntry);
                assert (res != null);

                stopwords.append(res);
            } else {
                final String res = getStopwordsFromFile(fileEntry);
                if (res == null) {
                    System.out.println("Failed to get stopwords from: " + fileEntry.getAbsolutePath());
                    System.exit(0);
                }

                stopwords.append(res);
            }
        }

        return stopwords.toString();
    }

    private static String getStopwordsFromFile(final File stopwordsFile) {
        try {
            final FileInputStream input = new FileInputStream(stopwordsFile);

            final InputStreamReader reader = new InputStreamReader(input, "UTF-8");
            final StringBuilder stopwordsBuffer = new StringBuilder();

            while (reader.ready()) {
                stopwordsBuffer.append(Character.toChars(reader.read()));
            }

            input.close();

            return stopwordsBuffer.toString();
        } catch (IOException ex) {
            Logger.getLogger(Stopwords.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }
}
