package evaluation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EvalReader {

    private final String resultsPath = "results.txt";
    private final String qrelsPath = "qrels.txt";

    HashMap<Integer, Integer> readRelativeDocs(int topicNo) {
        HashMap<Integer, Integer> relativeDocs = new HashMap<>();

        final File f = new File(qrelsPath);

        try {
            try (FileReader fr = new FileReader(f);
                    BufferedReader reader = new BufferedReader(fr)) {

                int doc, score, currTopicNo;
                while (reader.ready()) {
                    String line = reader.readLine();
                    String[] lineTokens = line.split("\t");

                    currTopicNo = Integer.parseInt(lineTokens[0]);
                    if (currTopicNo > topicNo) {
                        break;
                    } else if (currTopicNo == topicNo) {
                        doc = Integer.parseInt(lineTokens[2]);
                        score = Integer.parseInt(lineTokens[3]);

                        relativeDocs.put(doc, score);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Evaluation.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        } catch (IOException ex) {
            Logger.getLogger(Evaluation.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }

        return relativeDocs;
    }

    void readResults(int topicNo, Metrics metrics) {
        final File f = new File(resultsPath);

        try {
            try (FileReader fr = new FileReader(f);
                    BufferedReader reader = new BufferedReader(fr)) {

                int doc, currTopicNo;
                double score;
                while (reader.ready()) {
                    String line = reader.readLine();
                    String[] lineTokens = line.split(" ");

                    currTopicNo = Integer.parseInt(lineTokens[0]);
                    if (currTopicNo > topicNo) {
                        break;
                    } else if (currTopicNo == topicNo) {
                        doc = Integer.parseInt(lineTokens[2]);
                        score = Double.parseDouble(lineTokens[4]);

                        metrics.addResultData(doc, score);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Evaluation.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        } catch (IOException ex) {
            Logger.getLogger(Evaluation.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }
}
