package evaluation;

import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Evaluation {

    public static void main(String[] args) {
        int numberOfTopics = 30; // default

        try {
            System.out.println(args[0]);
            numberOfTopics = Integer.parseInt(args[0]);
        } catch (NumberFormatException ex) {
            Logger.getLogger(Evaluation.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        } catch (ArrayIndexOutOfBoundsException e) {

        }

        EvalReader reader = new EvalReader();

        HashMap<Integer, Integer> relativeDocs;

        Metrics.resetOutputData();

        for (int topicNo = 1; topicNo <= numberOfTopics; ++topicNo) {
            Metrics topicMetrics = new Metrics(topicNo);

            // read apo qrels.txt for topic number
            relativeDocs = reader.readRelativeDocs(topicNo);
            if (relativeDocs.isEmpty()) {
                System.out.println("not judged docs for topic: " + topicNo);
            }
            topicMetrics.setRelativeDocs(relativeDocs);

            // read apo results.txt for topic number
            reader.readResults(topicNo, topicMetrics);

            topicMetrics.calculateMetrics();
            topicMetrics.writeMetrics();
        }
    }
}
