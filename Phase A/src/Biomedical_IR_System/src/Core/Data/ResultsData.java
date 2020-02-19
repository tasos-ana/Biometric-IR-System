package Core.Data;

public class ResultsData {

    private String filePath;
    private String snippet;
    private double score;
    private final String pmcid;

    public ResultsData(final String filePath, final String snippet,
            final double score, final String pmcid) {

        this.filePath = filePath;
        this.snippet = snippet;
        this.score = score;
        this.pmcid = pmcid;
    }

    public void setFilePath(final String filePath) {
        this.filePath = filePath;
    }

    public void setSnippet(final String snippet) {
        this.snippet = snippet;
    }

    public void setScore(final double score) {
        this.score = score;
    }

    public String getFilePath() {
        return filePath;
    }

    public String getSnippet() {
        return snippet;
    }

    public double getScore() {
        return score;
    }

    public String getPMCID() {
        return pmcid;
    }
}
