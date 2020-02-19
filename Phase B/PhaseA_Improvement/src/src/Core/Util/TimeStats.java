package Core.Util;

public class TimeStats {

    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~  Analyze Terms ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ */
    private static long analyzeTerms_start_timeStamp = 0;
    private static long analyzeTerms_time = 0;

    public static void startTime_analyzeTerms() {
        assert (analyzeTerms_start_timeStamp == 0);
        analyzeTerms_start_timeStamp = System.currentTimeMillis();
    }

    public static void endTime_analyzeTerms() {
        assert (analyzeTerms_start_timeStamp != 0);
        long analyzeTerms_end_timeStamp = System.currentTimeMillis();
        analyzeTerms_time += analyzeTerms_end_timeStamp - analyzeTerms_start_timeStamp;
        analyzeTerms_start_timeStamp = 0;
    }

    public static void printTime_analyzeTerms() {
        System.out.println(analyzeTerms_toString());
    }

    private static String analyzeTerms_toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Analyze Terms Total: ")
                .append((double) analyzeTerms_time / 1000.0)
                .append(" sec\n");

        return sb.toString();
    }

    public static void reset_analyzeTerms() {
        analyzeTerms_time = 0;
    }
    // ------------------------------------------------------------------------

    /* ===========================  Vocabulary ============================= */
    private static long vocabulary_start_timeStamp = 0;
    private static long vocabulary_time = 0;

    public static void startTime_vocabulary() {
        assert (vocabulary_start_timeStamp == 0);
        vocabulary_start_timeStamp = System.currentTimeMillis();
    }

    public static void endTime_vocabulary() {
        assert (vocabulary_start_timeStamp != 0);
        long vocabulary_end_timeStamp = System.currentTimeMillis();
        vocabulary_time += vocabulary_end_timeStamp - vocabulary_start_timeStamp;
        vocabulary_start_timeStamp = 0;
    }

    public static void printTime_vocabulary() {
        System.out.println(vocabulary_toString());
    }

    private static String vocabulary_toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\tVocabulary: ")
                .append((double) vocabulary_time / 1000.0)
                .append(" sec\n");

        return sb.toString();
    }

    public static void reset_vocabulary() {
        vocabulary_time = 0;
    }
    // ------------------------------------------------------------------------

    /* =============================  Posting ============================== */
    private static long posting_start_timeStamp = 0;
    private static long posting_time = 0;

    public static void startTime_posting() {
        assert (posting_start_timeStamp == 0);
        posting_start_timeStamp = System.currentTimeMillis();
    }

    public static void endTime_posting() {
        assert (posting_start_timeStamp != 0);
        long posting_end_timeStamp = System.currentTimeMillis();
        posting_time += posting_end_timeStamp - posting_start_timeStamp;
        posting_start_timeStamp = 0;
    }

    public static void printTime_posting() {
        System.out.println(posting_toString());
    }

    private static String posting_toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\tPosting: ")
                .append((double) posting_time / 1000.0)
                .append(" sec\n");

        return sb.toString();
    }

    public static void reset_posting() {
        posting_time = 0;
    }
    // ------------------------------------------------------------------------

    /* ============================  Document ============================== */
    private static long document_start_timeStamp = 0;
    private static long document_time = 0;

    public static void startTime_document() {
        assert (document_start_timeStamp == 0);
        document_start_timeStamp = System.currentTimeMillis();
    }

    public static void endTime_document() {
        assert (document_start_timeStamp != 0);
        long document_end_timeStamp = System.currentTimeMillis();
        document_time += document_end_timeStamp - document_start_timeStamp;
        document_start_timeStamp = 0;
    }

    public static void printTime_document() {
        System.out.println(document_toString());
    }

    private static String document_toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\tDocument: ")
                .append((double) document_time / 1000.0)
                .append(" sec\n");

        return sb.toString();
    }

    public static void reset_document() {
        document_time = 0;
    }
    // ------------------------------------------------------------------------

    /* ===========================  weightsNorms =========================== */
    private static long weightsNorms_start_timeStamp = 0;
    private static long weightsNorms_time = 0;

    public static void startTime_weightsNorms() {
        assert (weightsNorms_start_timeStamp == 0);
        weightsNorms_start_timeStamp = System.currentTimeMillis();
    }

    public static void endTime_weightsNorms() {
        assert (weightsNorms_start_timeStamp != 0);
        long weightsNorms_end_timeStamp = System.currentTimeMillis();
        weightsNorms_time += weightsNorms_end_timeStamp - weightsNorms_start_timeStamp;
        weightsNorms_start_timeStamp = 0;
    }

    public static void printTime_weightsNorms() {
        System.out.println(weightsNorms_toString());
    }

    private static String weightsNorms_toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\tCalculate weights & norms: ")
                .append((double) weightsNorms_time / 1000.0)
                .append(" sec\n");

        return sb.toString();
    }

    public static void reset_weightsNorms() {
        weightsNorms_time = 0;
    }
    // ------------------------------------------------------------------------

    /* ==========================  Parse Query ============================= */
    private static long parseQuery_start_timeStamp = 0;
    private static long parseQuery_time = 0;

    public static void startTime_parseQuery() {
        assert (parseQuery_start_timeStamp == 0);
        parseQuery_start_timeStamp = System.currentTimeMillis();
    }

    public static void endTime_parseQuery() {
        assert (parseQuery_start_timeStamp != 0);
        long parseQuery_end_timeStamp = System.currentTimeMillis();
        parseQuery_time += parseQuery_end_timeStamp - parseQuery_start_timeStamp;
        parseQuery_start_timeStamp = 0;
    }

    public static void printTime_parseQuery() {
        System.out.println(parseQuery_toString());
    }

    private static String parseQuery_toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Parse Query: ")
                .append((double) parseQuery_time / 1000.0)
                .append(" sec\n");

        return sb.toString();
    }

    public static void reset_parseQuery() {
        parseQuery_time = 0;
    }
    // ------------------------------------------------------------------------
    // ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~  Calculate Results ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

    /* ========================  Calculate Snippet ========================= */
    private static long calculateSnippet_start_timeStamp = 0;
    private static long calculateSnippet_time = 0;

    public static void startTime_calculateSnippet() {
        assert (calculateSnippet_start_timeStamp == 0);
        calculateSnippet_start_timeStamp = System.currentTimeMillis();
    }

    public static void endTime_calculateSnippet() {
        assert (calculateSnippet_start_timeStamp != 0);
        long calculateSnippet_end_timeStamp = System.currentTimeMillis();
        calculateSnippet_time += calculateSnippet_end_timeStamp - calculateSnippet_start_timeStamp;
        calculateSnippet_start_timeStamp = 0;
    }

    public static void printTime_calculateSnippet() {
        System.out.println(calculateSnippet_toString());
    }

    private static String calculateSnippet_toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\tSnippets: ")
                .append((double) calculateSnippet_time / 1000.0)
                .append(" sec\n");

        return sb.toString();
    }

    public static void reset_calculateSnippet() {
        calculateSnippet_time = 0;
    }

    // ------------------------------------------------------------------------
    // ||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||||
    /* ~~~~~~~~~~~~~~~~~~~~~~~~~~~~  Get and Add Results ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

     ============================  Sort Results ============================ */
    private static long sortResults_start_timeStamp = 0;
    private static long sortResults_time = 0;

    public static void startTime_sortResults() {
        assert (sortResults_start_timeStamp == 0);
        sortResults_start_timeStamp = System.currentTimeMillis();
    }

    public static void endTime_sortResults() {
        assert (sortResults_start_timeStamp != 0);
        long sortResults_end_timeStamp = System.currentTimeMillis();
        sortResults_time += sortResults_end_timeStamp - sortResults_start_timeStamp;
        sortResults_start_timeStamp = 0;
    }

    public static void printTime_sortResults() {
        System.out.println(sortResults_toString());
    }

    private static String sortResults_toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Sort Results: ")
                .append((double) sortResults_time / 1000.0)
                .append(" sec\n");

        return sb.toString();
    }

    public static void reset_sortResults() {
        sortResults_time = 0;
    }
    // ------------------------------------------------------------------------

    /* ========================  Add Results ========================= */
    private static long addResults_start_timeStamp = 0;
    private static long addResults_time = 0;

    public static void startTime_addResults() {
        assert (addResults_start_timeStamp == 0);
        addResults_start_timeStamp = System.currentTimeMillis();
    }

    public static void endTime_addResults() {
        assert (addResults_start_timeStamp != 0);
        long addResults_end_timeStamp = System.currentTimeMillis();
        addResults_time += addResults_end_timeStamp - addResults_start_timeStamp;
        addResults_start_timeStamp = 0;
    }

    public static void printTime_addResults() {
        System.out.println(addResults_toString());
    }

    private static String addResults_toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Add Results: ")
                .append((double) addResults_time / 1000.0)
                .append(" sec\n");

        return sb.toString();
    }

    public static void reset_addResults() {
        addResults_time = 0;
    }
    // ------------------------------------------------------------------------

    /* ========================  Write Results ========================= */
    private static long writeResults_start_timeStamp = 0;
    private static long writeResults_time = 0;

    public static void startTime_writeResults() {
        assert (writeResults_start_timeStamp == 0);
        writeResults_start_timeStamp = System.currentTimeMillis();
    }

    public static void endTime_writeResults() {
        assert (writeResults_start_timeStamp != 0);
        long writeResults_end_timeStamp = System.currentTimeMillis();
        writeResults_time += writeResults_end_timeStamp - writeResults_start_timeStamp;
        writeResults_start_timeStamp = 0;
    }

    public static void printTime_writeResults() {
        System.out.println(writeResults_toString());
    }

    private static String writeResults_toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Write Results: ")
                .append((double) writeResults_time / 1000.0)
                .append(" sec\n");

        return sb.toString();
    }

    public static void reset_writeResults() {
        writeResults_time = 0;
    }
    // ------------------------------------------------------------------------

    /* ===============================  Total =============================== */
    private static long total_start_timeStamp = 0;
    private static long total_time = 0;

    public static void startTime_total() {
        assert (total_start_timeStamp == 0);
        total_start_timeStamp = System.currentTimeMillis();
    }

    public static void endTime_total() {
        assert (total_start_timeStamp != 0);
        long total_end_timeStamp = System.currentTimeMillis();
        total_time += total_end_timeStamp - total_start_timeStamp;
        total_start_timeStamp = 0;
    }

    public static void printTime_total() {
        System.out.println(total_toString());
    }

    private static String total_toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nTotal: ")
                .append((double) total_time / 1000.0)
                .append(" sec\n");

        return sb.toString();
    }

    public static void reset_total() {
        total_time = 0;
    }
    // ------------------------------------------------------------------------

    /* ===============================  Topic =============================== */
    private static long topic_start_timeStamp = 0;
    private static long topic_time = 0;

    public static void startTime_topic() {
        assert (topic_start_timeStamp == 0);
        topic_start_timeStamp = System.currentTimeMillis();
    }

    public static void endTime_topic() {
        assert (topic_start_timeStamp != 0);
        long test_end_timeStamp = System.currentTimeMillis();
        topic_time += test_end_timeStamp - topic_start_timeStamp;
        topic_start_timeStamp = 0;
    }

    public static void printTime_topic(int topicNumber) {
        System.out.println(topic_toString(topicNumber));
    }

    private static String topic_toString(int topicNumber) {
        StringBuilder sb = new StringBuilder();
        sb.append("Topic ").append(topicNumber).append(" total: ")
                .append((double) topic_time / 1000.0).append(" sec\n")
                .append("----------------------------------------\n");

        return sb.toString();
    }

    public static void reset_topic() {
        topic_time = 0;
    }

    public static double getTopicTime() {
        return (double) topic_time / 1000.0;
    }

    // ------------------------------------------------------------------------
    public static double getTotal() {
        return (double) total_time / 1000.0;
    }

    public static void print_timeStatsTopic(int topicNumber) {
        System.out.println(timeStatsTopic_toString(topicNumber));
    }

    public static String timeStatsTopic_toString(int topicNumber) {
        assert (addResults_time == 0);

        StringBuilder sb = new StringBuilder();

        sb.append(" =============== Topic: ")
                .append(topicNumber).append(" ===============\n")
                .append("\nRetrieved Data stats: \n")
                .append(vocabulary_toString())
                .append(posting_toString())
                .append(document_toString())
                .append(weightsNorms_toString())
                .append(analyzeTerms_toString()).append("\n")
                .append(writeResults_toString()).append("\n")
                .append(topic_toString(topicNumber));

        return sb.toString();
    }
    
    // -------------------------------------

    public static void print_timeStatsNotTotal() {
//        printTime_parseQuery();
        System.out.println("\nRetrieved Data stats: ");
        printTime_vocabulary();
        printTime_posting();
        printTime_document();
        printTime_weightsNorms();
        printTime_calculateSnippet();
        printTime_analyzeTerms();
//        System.out.println();
//        printTime_sortResults();
        System.out.println();

        assert (addResults_time != 0);
        printTime_addResults();

    }

    public static void print_timeStats() {
        print_timeStatsNotTotal();
        printTime_total();
    }

    public static void reset_timeStatsNotTotal() {
        reset_analyzeTerms();
        reset_vocabulary();
        reset_posting();
        reset_document();
        reset_weightsNorms();
        reset_parseQuery();
        reset_calculateSnippet();
        reset_sortResults();
        reset_addResults();
        reset_writeResults();
//        reset_total();

        reset_topic();
    }

    public static void reset_timeStats() {
        reset_timeStatsNotTotal();
        reset_total();
    }
}
