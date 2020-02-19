package evaluation;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Metrics {

    private final int topicNo;
    private double Bpref;
    private double AveP;
    private double NDCG;
    int R;

    private static final String output = "eval_results.txt";

    private class resultsData {

        private final int doc;

        private resultsData(int doc, double score) {
            this.doc = doc;
        }
    }

    HashMap<Integer, Integer> relativeDocs;
    ArrayList<resultsData> results;
    ArrayList<Integer> resultsRelScores = new ArrayList<>();

    Metrics(int topicNo) {
        this.topicNo = topicNo;

        R = -1;
        Bpref = -1;
        AveP = -1;
        NDCG = -1;

        results = new ArrayList<>();
        resultsRelScores = new ArrayList<>();
    }

    public static void resetOutputData() {
        File f = new File(output);
        if (f.exists()) {
            f.delete();
        }
    }

    public void setRelativeDocs(HashMap<Integer, Integer> relativeDocs) {
        this.relativeDocs = relativeDocs;
    }

    public void addResultData(int doc, double score) {
        results.add(new resultsData(doc, score));
    }

    public void calculateMetrics() {
        calculateJudgedRelevantNumber();

        calculateBpref();
        calculateAveP();
        calculateNDCG();
    }

    public void writeMetrics() {
        assert (R != -1);
        assert (Bpref != -1);
        assert (AveP != -1);
        assert (NDCG != -1);

        try {
            try (FileWriter fw = new FileWriter(output, true);
                    BufferedWriter writer = new BufferedWriter(fw)) {

                StringBuilder sb = new StringBuilder();

                sb.append(Integer.toString(topicNo))
                        .append(" ")
                        .append(Double.toString(Bpref))
                        .append(" ")
                        .append(Double.toString(AveP))
                        .append(" ")
                        .append(Double.toString(NDCG))
                        .append("\n");

                writer.write(sb.toString());

            }
        } catch (IOException ex) {
            Logger.getLogger(Metrics.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }
    }

    private void calculateJudgedRelevantNumber() {
        R = 0;

        ArrayList<Integer> relScores = new ArrayList<>(relativeDocs.values());
        for (int i = 0; i < relScores.size(); ++i) {
            if (relScores.get(i) != 0) {
                ++R;
            }
        }
    }

    private void calculateBpref() {
        int irrelevantCounter = 0;
        double BprefSum = 0;

        Bpref = 0;
        for (int i = 0; i < results.size(); ++i) {
            if (relativeDocs.containsKey(results.get(i).doc)) {
                if (relativeDocs.get(results.get(i).doc) != 0) {
                    BprefSum += (1 - (double) irrelevantCounter / R);
                } else if (irrelevantCounter < R) { // mexri ta R prwta irrelevant
                    ++irrelevantCounter;
                }
            }
        }

        Bpref = ((double) 1 / R) * BprefSum;
    }

    private void calculateAveP() {
        int irrelevantCounter = 0, judgedRank = 0;
        double AvePSum = 0;

        AveP = 0;
        for (int i = 0; i < results.size(); ++i) {
            if (relativeDocs.containsKey(results.get(i).doc)) {
                ++judgedRank;
                if (relativeDocs.get(results.get(i).doc) != 0) {
                    AvePSum += (1 - (double) irrelevantCounter / judgedRank); // rank(doc) xwris ta unjudged
                } else {
                    ++irrelevantCounter;
                }
            }
        }

        AveP = ((double) 1 / R) * AvePSum;
    }

    private void calculateNDCG() {
        double dcg = calculateDCG();
        double idcg = calculateIDCG();

        if (dcg == 0) {
            NDCG = 0;
        } else {
            NDCG = dcg / idcg;
        }
    }

    private double calculateDCG() {
        double dcg = 0;
        int found = 0;

        for (int i = 1; i <= results.size(); ++i) {
            int rel_i;

            if (relativeDocs.containsKey(results.get(i - 1).doc)) {
                ++found;

                rel_i = relativeDocs.get(results.get(i - 1).doc);

                resultsRelScores.add(rel_i);

                double log_2 = Math.log(found + 1) / Math.log(2);

                dcg += (Math.pow(2, rel_i) - 1) / log_2;
            }
            // ta doc pou einai sta result alla oxi sto qrels.txt
            // ta prospername xwris na au3anetai h thesi (founds)..
            // wste na min epireastei to apotelesma,
            // opote ta unjudged aplws agnoountai
        }

        return dcg;
    }

    private double calculateIDCG() {
        double idcg = 0;

        Collections.sort(resultsRelScores);
        Collections.reverse(resultsRelScores);

        for (int i = 1; i <= resultsRelScores.size(); ++i) {
            double log_2 = Math.log(i + 1) / Math.log(2);
            idcg += (Math.pow(2, resultsRelScores.get(i - 1)) - 1) / log_2;
        }

        return idcg;
    }

}
