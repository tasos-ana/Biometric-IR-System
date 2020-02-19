package GraphicUI;

import Controller.searchingController;
import Core.Operations.TextReader;
import Core.Util.Stopwords;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import static java.lang.System.exit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

public class SearchingGui {

    private final searchingController controller;

    private JFrame win;
    private JFrame topicFrame;

    private JDesktopPane pan;
    private JPanel results;
    private JLabel searchingTimeText;
    private JScrollPane sPanel;

    private JLabel queryLabel;
    private JLabel typeLabel;

    private JTextField queryText;
    private JComboBox typeText;

    private JSeparator sep;

    private JButton loadTopicBut;
    private JButton searchBut;

    private JLabel topicPath;
    private JComboBox extractQueryFrom;

    public SearchingGui() throws IOException {
        if (!Stopwords.isValid("5_Resources_Stoplists")) {
            JOptionPane.showMessageDialog(pan, "Please make sure that "
                    + "<5_Resources_Stoplists> placed on same folder with program and re-run program");
            exit(0);
        }
        topicPath = null;
        extractQueryFrom = null;
        controller = new searchingController();
        initComponents();
        controller.setStopwords("5_Resources_Stoplists");
        boolean done = controller.loadVocabulary("CollectionIndex");
        if (!done) {
            JOptionPane.showMessageDialog(pan, "Please make sure that "
                    + "<CollectionIndex> placed on same folder with program and re-run program");
            exit(0);
        }
        win.setVisible(true);
    }

    private void chooseInputFile(JLabel path) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int returnVal = chooser.showOpenDialog(pan);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            path.setText(chooser.getSelectedFile().getAbsolutePath());
        } else {
            path.setText(" . . . ");
        }
    }

    private void initComponents() {
        win = new JFrame("Biomedical Searcher");
        win.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        win.setPreferredSize(new Dimension(800, 600));
        win.setSize(800, 600);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        win.setLocation(dim.width / 2 - win.getSize().width / 2,
                dim.height / 2 - win.getSize().height / 2);
        win.setResizable(false);

        pan = new JDesktopPane();

        queryLabel = new JLabel("Query: ");
        queryLabel.setBounds(20, 10, 50, 30);

        queryText = new JTextField();
        queryText.setBounds(70, 10, 200, 30);

        typeLabel = new JLabel("Type: ");
        typeLabel.setBounds(280, 10, 50, 30);

        String typeCombo[] = {"None", "Diagnosis", "Test", "Treatment"};
        typeText = new JComboBox(typeCombo);
        typeText.setSelectedIndex(0);
        typeText.setBounds(320, 10, 120, 30);

        loadTopicBut = new JButton("Load topic...");
        loadTopicBut.setBounds(500, 10, 110, 30);
        loadTopicBut.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loadTopicBut.addActionListener((ActionEvent e) -> {
            loadTopicButPressed();
        });

        searchBut = new JButton(" Search ");
        searchBut.setBounds(620, 10, 130, 30);
        searchBut.setCursor(new Cursor(Cursor.HAND_CURSOR));
        searchBut.addActionListener((ActionEvent e) -> {
            searchButPressed();
        });

        sep = new JSeparator();
        sep.setBounds(0, 50, 800, 10);

        initResultComponent();

        pan.add(queryLabel);
        pan.add(queryText);
        pan.add(typeLabel);
        pan.add(typeText);
        pan.add(searchBut);
        pan.add(loadTopicBut);
        pan.add(sep);

        win.add(pan);
        win.pack();
    }

    private void initResultComponent() {
        if (sPanel != null) {
            pan.remove(sPanel);
        }
        results = new JPanel();
        results.setLayout(null);
        sPanel = new JScrollPane(results);
        sPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        sPanel.setBounds(10, 60, 780, 500);
        results.setBackground(Color.white);

        searchingTimeText = new JLabel();
        searchingTimeText.setBounds(5, 0, 780, 20);
        results.add(searchingTimeText);
        pan.add(sPanel);
    }

    public void addResult(int position, String filePath, String msg, double score) {
        JPanel p = new JPanel();
        p.setLayout(null);
        p.setBounds(10, (position - 1) * 65 + 20, 745, 60);

        JLabel num, snipet, scoreText;
        JButton file = new JButton(filePath);
        file.setBounds(30, 5, filePath.length() * 7 + 10, 20);
        file.addActionListener(new resultAction(filePath));
        file.setCursor(new Cursor(Cursor.HAND_CURSOR));

        num = new JLabel(position + ".");
        num.setBounds(0, 20, 50, 30);

        snipet = new JLabel(msg);
        snipet.setBounds(30, 20, 750, 30);

        scoreText = new JLabel("Score: " + score);
        scoreText.setBounds(30, 35, 750, 30);

        p.add(num);
        p.add(snipet);
        p.add(scoreText);
        p.add(file);
        results.add(p);
        results.setPreferredSize(new Dimension(50, 70 * position));

        results.updateUI();
    }

    private void updateTimeResults(double time, int results) {
        searchingTimeText.setText("Searching time: " + time + " seconds. Total results: " + results);
    }

    private class resultAction implements ActionListener {

        private String path;

        public resultAction(String path) {
            this.path = path;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JTextArea textArea = new JTextArea();
            try {
                textArea.setText(TextReader.readFile(path));
            } catch (IOException ex) {
                Logger.getLogger(SearchingGui.class.getName()).log(Level.SEVERE, null, ex);
                exit(0);
            }
            JScrollPane scrollPane = new JScrollPane(textArea);
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);
            scrollPane.setPreferredSize(new Dimension(500, 500));
            JOptionPane.showMessageDialog(null, scrollPane, "File: \"" + path + "\"",
                    JOptionPane.YES_NO_OPTION);
        }
    }

    private void loadTopicButPressed() {
        topicFrame = new JFrame("Search from topic");
        topicFrame.setSize(200, 170);
        topicFrame.setResizable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        topicFrame.setLocation(dim.width / 2 - topicFrame.getSize().width / 2,
                dim.height / 2 - topicFrame.getSize().height / 2);

        JButton collectionBut = new JButton("Choose topic...");
        collectionBut.setCursor(new Cursor(Cursor.HAND_CURSOR));
        collectionBut.setBounds(20, 10, 150, 20);
        collectionBut.addActionListener((ActionEvent e) -> {
            chooseInputFile(topicPath);
        });

        topicPath = new JLabel(" . . . ");
        topicPath.setHorizontalAlignment(SwingConstants.CENTER);
        topicPath.setBounds(20, 30, 150, 30);

        String typeCombo[] = {"Description", "Summary"};
        extractQueryFrom = new JComboBox(typeCombo);
        extractQueryFrom.setSelectedIndex(0);
        extractQueryFrom.setBounds(20, 70, 150, 20);

        JButton topicBut = new JButton(" Continue ");
        topicBut.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topicBut.setBounds(50, 100, 100, 30);
        topicBut.addActionListener((ActionEvent e) -> {
            topicButPressed();
        });

        JDesktopPane pan2 = new JDesktopPane();
        pan2.add(topicPath);
        pan2.add(collectionBut);
        pan2.add(extractQueryFrom);
        pan2.add(topicBut);
        topicFrame.add(pan2);
        topicFrame.setVisible(true);
    }

    private void searchButPressed() {
        initResultComponent();

        String query, type;
        query = queryText.getText();
        type = (String) typeText.getSelectedItem();
        if (query.trim().isEmpty()) {
            JOptionPane.showMessageDialog(pan, "Please type something on query text area before start searching");
        } else {
            final long tStart = System.currentTimeMillis();
            int res = controller.makeSearch(query, type);
            final long tEnd = System.currentTimeMillis();
            final long tDelta = tEnd - tStart;
            final double elapsedSeconds = tDelta / 1000.0;
            updateTimeResults(elapsedSeconds, res);
        }
    }

    private void topicButPressed() {
        if (topicPath.getText().equals(" . . . ")) {
            JOptionPane.showMessageDialog(new JDesktopPane(), "Please choose topic path");
            return;
        }
        String path = topicPath.getText();
        String loadFrom = (String) extractQueryFrom.getSelectedItem();
        loadFrom = loadFrom.toLowerCase();
        final long tStart = System.currentTimeMillis();

        int res = controller.searchTopics(path, loadFrom);

        final long tEnd = System.currentTimeMillis();
        final long tDelta = tEnd - tStart;
        final double elapsedSeconds = tDelta / 1000.0;

        searchingTimeText.setText("Searching time: " + elapsedSeconds + " seconds. Total topics: " + res);

        JOptionPane.showMessageDialog(new JDesktopPane(), "Searching complete. Results placed in the same folder as the topic.xml");
        topicPath = null;
        topicFrame.setVisible(false);
    }

    public void noResults() {
        JOptionPane.showMessageDialog(pan, "No results found");
    }
}
