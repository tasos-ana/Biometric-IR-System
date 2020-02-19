package Core.Data;

import Core.Operations.TextReader;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TermInfo {

    //vocabulary
    private String term;
    private int df;
    private int pointer;

    //posting file
    private int docID;
    private double tf;
    private String tag;
    private int position;

    //documents
    private String filePath;
    private double norm;

    //calculated
    private static double queryNorm;

    private double docWeight; //tf*idf
    private double queryWeight;

    private String snippet;
    private double score;

    private String pmcid;

    public TermInfo() {
        snippet = "Snippet";
        pmcid = "-1";
    }

    public void retrievePostingInfo(final String postingPath, final int i) {
        try {
            RandomAccessFile file = new RandomAccessFile(postingPath, "r");
            //to 1o stoixeio exei pointer 1 ara kanoume -1 gia na deiksoume stin arxh tou file
            file.seek((pointer - 1 + i) * 16);

            docID = file.readInt();
            final int fileTF = file.readInt();
            if (fileTF == -1) {
                tf = 1.0;
            } else {
                tf = Double.parseDouble("0." + Integer.toString(fileTF));
            }
            tag = Tags.num2tag(file.readInt());
            position = file.readInt();

            file.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TextReader.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TextReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void retrieveDocumentsInfo(final String documentsPath) {
        final File f = new File(documentsPath);
        String line = null;
        try {
            final FileReader fr = new FileReader(f);
            final BufferedReader reader = new BufferedReader(fr);
            reader.readLine(); // 1st line is number of docs
            int cnt = 1;
            while (reader.ready()) {
                line = reader.readLine();
                if (cnt == docID) {
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
        assert (line != null);
        final String[] arr = line.split(" ");
        filePath = arr[1];
        norm = Double.parseDouble(arr[2]);
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(final String term) {
        this.term = term;
    }

    public int getDocID() {
        return docID;
    }

    public void setDocID(final int docID) {
        this.docID = docID;
    }

    public double getScore() {
        return score;
    }

    public void setScore(final double score) {
        this.score = score;
    }

    public int getDf() {
        return df;
    }

    public void setDf(final int df) {
        this.df = df;
    }

    public int getPointer() {
        return pointer;
    }

    public void setPointer(final int pointer) {
        this.pointer = pointer;
    }

    public double getTf() {
        return tf;
    }

    public void setTf(final double tf) {
        this.tf = tf;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(final String tag) {
        this.tag = tag;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(final int position) {
        this.position = position;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(final String filePath) {
        this.filePath = filePath;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(final String snippet) {
        this.snippet = snippet;
    }

    public double getNorm() {
        return norm;
    }

    public void setNorm(final double norm) {
        this.norm = norm;
    }

    public static double getQueryNorm() {
        return queryNorm;
    }

    public static void increaseQueryNorm(final double queryNorm) {
        TermInfo.queryNorm += queryNorm;
    }

    public static void setQueryNorm(final double queryNorm) {
        TermInfo.queryNorm = queryNorm;
    }

    public double getDocWeight() {
        return docWeight;
    }

    public void setDocWeight(final double docWeight) {
        this.docWeight = docWeight;
    }

    public double getQueryWeight() {
        return queryWeight;
    }

    public void setQueryWeight(final double queryWeight) {
        this.queryWeight = queryWeight;
    }

    public void setPMCID(final String pmcid) {
        this.pmcid = pmcid;
    }

    public String getPMCID() {
        return pmcid;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nTerm: ").append(term);
        sb.append("\nDF: ").append(df);
        sb.append("\nPointer: ").append(pointer);
        sb.append("\nDocumentID: ").append(docID);
        sb.append("\nTF: ").append(tf);
        sb.append("\nTag: ").append(tag);
        sb.append("\nPosition: ").append(position);
        sb.append("\nFilePath: ").append(filePath);
        sb.append("\nNorm: ").append(norm);
        sb.append("\nQueryNorm: ").append(queryNorm);
        sb.append("\nDocumentWeight: ").append(docWeight);
        sb.append("\nQueryWeight: ").append(queryWeight);
        sb.append("\nSnippet: ").append(snippet);
        sb.append("\nScore: ").append(score);
        sb.append("\nPMCID: ").append(pmcid);
        sb.append("\n\n");
        return sb.toString();
    }
}
