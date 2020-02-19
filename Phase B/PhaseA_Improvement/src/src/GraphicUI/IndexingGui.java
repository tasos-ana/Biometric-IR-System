package GraphicUI;

import Controller.indexingController;
import Core.Util.Stopwords;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import static java.lang.System.exit;
import javax.swing.JButton;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

public class IndexingGui {

    private final indexingController controller;

    private JFrame win;

    private JButton collectionBut;

    private JButton indexingBut;

    private JTextArea outputArea;
    private JScrollPane outputAreaScroll;

    private JSeparator sep1, sep2;
    private JProgressBar progress;

    private JDesktopPane pan;

    public IndexingGui() {
        if (!Stopwords.isValid("5_Resources_Stoplists")) {
            JOptionPane.showMessageDialog(pan, "Please make sure that "
                    + "<5_Resources_Stoplists> placed on same folder with program and re-run program");
            exit(0);
        }
        controller = new indexingController();
        initComponents();
    }

    private void initComponents() {
        win = new JFrame("Biomedical Indexer");
        win.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        win.setLocation(new Point(50, 50));
        win.setPreferredSize(new Dimension(600, 400));
        win.setResizable(false);

        collectionBut = new JButton("Choose collection...");
        collectionBut.setBounds(215, 10, 160, 20);
        collectionBut.setCursor(new Cursor(Cursor.HAND_CURSOR));
        collectionBut.addActionListener((ActionEvent e) -> {
            collectionButPressed();
        });

        outputArea = new JTextArea();
        outputAreaScroll = new JScrollPane(outputArea);
        outputArea.setFont(new Font("Arial", 0, 12));
        outputArea.setText(" Output log: \n");
        outputArea.setEditable(false);
        outputAreaScroll.setBounds(3, 48, 590, 290);
        outputAreaScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        indexingBut = new JButton("Start Indexing");
        indexingBut.setBounds(385, 345, 160, 20);
        indexingBut.setCursor(new Cursor(Cursor.HAND_CURSOR));
        indexingBut.addActionListener((ActionEvent e) -> {
            indexingButPressed();
        });

        sep1 = new JSeparator();
        sep1.setBounds(0, 40, 600, 10);

        progress = new JProgressBar(0, 100);
        progress.setBounds(40, 345, 335, 20);

        sep2 = new JSeparator();
        sep2.setBounds(0, 340, 600, 10);

        pan = new JDesktopPane();

        pan.add(collectionBut, JLayeredPane.DEFAULT_LAYER);
        pan.add(sep1);
        pan.add(outputAreaScroll);
        pan.add(progress);
        pan.add(sep2);
        pan.add(indexingBut);

        win.add(pan);
        win.pack();
        win.setVisible(true);
    }

    private void collectionButPressed() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = chooser.showOpenDialog(pan);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().getAbsolutePath();
            controller.updateCollection(path);
            outputArea.append("\nCollection path:\t " + "<<" + path + ">>");
        } else {
            controller.updateCollection(null);
        }
    }

    private void indexingButPressed() {
        if (!controller.isReady()) {
            JOptionPane.showMessageDialog(pan, "Please choose input & output file before start indexing");
        } else {
            prepare();
            controller.execute();
        }
    }

    public void updateLog(String msg) {
        outputArea.append(msg);
    }

    public JProgressBar getProgress() {
        return progress;
    }

    private void prepare() {
        indexingBut.setEnabled(false);
        collectionBut.setEnabled(false);
        progress.setIndeterminate(true);
    }

    public void complete() {
        progress.setIndeterminate(false);
        JOptionPane.showMessageDialog(pan, "Indexing complete successfull!");
    }

}
