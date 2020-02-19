package GraphicUI;

import Controller.searchingController;
import Core.Operations.TextReader;
import Core.Util.Stopwords;
import Core.Util.TimeStats;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
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
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;

/**
 *
 * @author Tasos
 */
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

    private String topicPathString;
    private JButton topicPath;
    private JButton topicCancelBut;
    private JButton topicStartBut;
    private JProgressBar topicProgBar;
    private JTextArea topicOutputLog;
    private JComboBox extractQueryFrom;

    WindowListener exitListener = new WindowAdapter() {
        @Override
        public void windowClosing(WindowEvent e) {
            cancelTopicButPressed();
        }
    };

    ActionListener cancelLoadTopicListener = (ActionEvent e) -> {
        cancelTopicButPressed();
    };

    ActionListener cancelWorkerListener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent e) {
            controller.cancelTopicSearch();
            TimeStats.reset_timeStats();
            //controller.cancel(true);
            topicCancelBut.setEnabled(false);
            topicStartBut.setText("Canceling...");
        }
    };

    public SearchingGui() throws IOException {
        if (!Stopwords.isValid("5_Resources_Stoplists")) {
            JOptionPane.showMessageDialog(pan, "Please make sure that "
                    + "<5_Resources_Stoplists> placed on same folder with program and re-run program");
            exit(0);
        }
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

    private void chooseInputFile() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        int returnVal = chooser.showOpenDialog(pan);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            topicPathString = chooser.getSelectedFile().getAbsolutePath();
            updateLog("Topic path: " + topicPathString + "\n");
        } else if (topicPathString == null) {
            updateLog("Topic path: not setted yet\n");
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

        final private String path;

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
        topicFrame = new JFrame(" Search topics ");
        topicFrame.setSize(600, 600);
        topicFrame.setResizable(false);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        topicFrame.setLocation(dim.width / 2 - topicFrame.getSize().width / 2,
                dim.height / 2 - topicFrame.getSize().height / 2);

        topicFrame.addWindowListener(exitListener);

        topicPath = new JButton("Choose path");
        topicPath.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topicPath.setBounds(20, 10, 130, 20);
        topicPath.addActionListener((ActionEvent e) -> {
            chooseInputFile();
        });

        String typeCombo[] = {"Description", "Summary"};
        extractQueryFrom = new JComboBox(typeCombo);
        extractQueryFrom.setSelectedIndex(0);
        extractQueryFrom.setBounds(160, 10, 130, 20);

        topicCancelBut = new JButton("Cancel");
        topicCancelBut.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topicCancelBut.setBounds(330, 10, 120, 20);
        topicCancelBut.addActionListener(cancelLoadTopicListener);

        topicStartBut = new JButton("Start");
        topicStartBut.setCursor(new Cursor(Cursor.HAND_CURSOR));
        topicStartBut.setBounds(460, 10, 120, 20);
        topicStartBut.addActionListener((ActionEvent e) -> {
            startTopicButPressed();
        });

        topicProgBar = new JProgressBar(0, 100);
        topicProgBar.setBounds(0, 45, 600, 20);

        JSeparator sep1 = new JSeparator();
        sep1.setBounds(0, 70, 600, 5);

        topicOutputLog = new JTextArea();
        JScrollPane outputAreaScroll = new JScrollPane(topicOutputLog);
        topicOutputLog.setFont(new Font("Arial", 0, 12));
        topicOutputLog.setEditable(false);
        outputAreaScroll.setBounds(3, 75, 595, 523);
        outputAreaScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        JDesktopPane pan2 = new JDesktopPane();
        pan2.add(topicPath);
        pan2.add(extractQueryFrom);
        pan2.add(topicCancelBut);
        pan2.add(topicStartBut);
        pan2.add(topicProgBar);
        pan2.add(sep1);
        pan2.add(outputAreaScroll);
        topicFrame.add(pan2);
        win.setVisible(false);
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
            TimeStats.reset_timeStats();
            TimeStats.startTime_total();
            int res = controller.makeSearch(query, type);
            TimeStats.endTime_total();
            TimeStats.print_timeStats();
            updateTimeResults(TimeStats.getTotal(), res);
        }
    }

    private void cancelTopicButPressed() {
        topicPathString = null;
        topicFrame.setVisible(false);
        win.setVisible(true);
    }

    private void startTopicButPressed() {
        if (topicPathString == null) {
            JOptionPane.showMessageDialog(new JDesktopPane(), "Please choose topic path");
            return;
        }
        String loadFrom = (String) extractQueryFrom.getSelectedItem();
        loadFrom = loadFrom.toLowerCase();

        topicCancelBut.addActionListener(cancelWorkerListener);
        topicCancelBut.removeActionListener(cancelLoadTopicListener);

        topicStartBut.setEnabled(false);
        extractQueryFrom.setEnabled(false);
        topicPath.setEnabled(false);
        topicStartBut.setText("Searching...");
        topicProgBar.setIndeterminate(true);
        topicFrame.removeWindowListener(exitListener);
        topicFrame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        controller.searchTopics(topicPathString, loadFrom);
    }

    public void topicSearchComplete() {
        JOptionPane.showMessageDialog(new JDesktopPane(), "Searching complete. Results placed in the same folder as the topic.xml");
        topicPathString = null;
        topicStartBut.setText("Finish");
        topicCancelBut.setEnabled(true);
        topicCancelBut.setText("Back");
        topicCancelBut.addActionListener(cancelLoadTopicListener);
        topicCancelBut.removeActionListener(cancelWorkerListener);

        topicFrame.addWindowListener(exitListener);
        topicProgBar.setIndeterminate(false);
    }

    public void topicSearchCanceled() {
        topicPathString = null;
        topicStartBut.setText("Canceled");
        topicCancelBut.setText("Back");
        topicCancelBut.setEnabled(true);
        topicCancelBut.addActionListener(cancelLoadTopicListener);
        topicCancelBut.removeActionListener(cancelWorkerListener);

        topicFrame.addWindowListener(exitListener);
        topicProgBar.setIndeterminate(false);
    }

    public void updateLog(String string) {
        topicOutputLog.append(string);
    }

    public void noResults() {
        topicPathString = null;
        topicFrame.setVisible(false);
        topicStartBut.setEnabled(false);
        topicStartBut.setText("Finish");
        topicCancelBut.setEnabled(true);
        topicCancelBut.setText("Back");
        topicProgBar.setIndeterminate(false);
        JOptionPane.showMessageDialog(pan, "No results found");
    }
}
