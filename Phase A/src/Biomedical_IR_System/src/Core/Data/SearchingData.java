package Core.Data;

public class SearchingData {

    private final Vocabulary words;

    private String collectionRoot;
    private String vocabularyPath;
    private String postingPath;
    private String documentsPath;

    public SearchingData() {
        this.words = new Vocabulary();
    }

    public void setCollectionRoot(final String collectionRoot) {
        this.collectionRoot = collectionRoot;
    }

    public void setVocabularyPath(final String vocabularyPath) {
        this.vocabularyPath = vocabularyPath;
    }

    public void setPostingPath(final String postingPath) {
        this.postingPath = postingPath;
    }

    public void setDocumentsPath(final String documentsPath) {
        this.documentsPath = documentsPath;
    }

    public boolean isReady() {
        return (collectionRoot != null && vocabularyPath != null
                && postingPath != null && documentsPath != null);
    }

    public Vocabulary getWords() {
        return words;
    }

    public String getVocabularyPath() {
        return vocabularyPath;
    }

    public String getPostingPath() {
        return postingPath;
    }

    public String getDocumentsPath() {
        return documentsPath;
    }

    public int getDF(final String term) {
        Occurrences termValue = words.getValue(term);
        if (termValue == null) {
            return -1;
        }
        return termValue.getDf();
    }

    public int getPointer(final String term) {
        Occurrences termValue = words.getValue(term);
        if (termValue == null) {
            return -1;
        }
        return termValue.getDocPointer();
    }

}
