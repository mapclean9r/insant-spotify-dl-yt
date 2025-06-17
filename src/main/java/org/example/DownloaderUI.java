package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class DownloaderUI {
    private final JFrame frame;
    private final JTextField urlField;
    private final JButton downloadButton;
    private final JButton chooseFolderButton;
    private final JLabel folderLabel;
    private final JTextArea logArea;
    private final DefaultListModel<String> downloadListModel;
    private final JList<String> downloadList;

    private final YouTubeDownloader downloader;
    private final UserSettings settings;
    private final DownloadHistory history;
    private File downloadDirectory;

    public DownloaderUI() {
        frame = new JFrame("YouTube mp3 dl");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);

        urlField = new JTextField();
        downloadButton = new JButton("💾 Download");
        chooseFolderButton = new JButton("📂 Choose folder");
        folderLabel = new JLabel("No folder selected");
        logArea = new JTextArea();
        logArea.setEditable(false);

        logArea.setForeground(Color.WHITE);

        downloadListModel = new DefaultListModel<>();
        downloadList = new JList<>(downloadListModel);
        downloadList.setBorder(BorderFactory.createTitledBorder("📄 Downloaded"));

        downloader = new YouTubeDownloader();
        settings = new UserSettings();
        history = new DownloadHistory();

        this.downloadDirectory = settings.getDownloadDirectory();
        history.getHistory().forEach(downloadListModel::addElement);

        setupLayout();
        setupListeners();
        updateFolderLabel();
    }

    private void setupLayout() {
        JPanel inputPanel = new JPanel(new BorderLayout(10, 10));
        inputPanel.add(new JLabel("YouTube-link:"), BorderLayout.WEST);
        inputPanel.add(urlField, BorderLayout.CENTER);
        inputPanel.add(downloadButton, BorderLayout.EAST);

        JPanel folderPanel = new JPanel(new BorderLayout(10, 10));
        folderPanel.add(chooseFolderButton, BorderLayout.WEST);
        folderPanel.add(folderLabel, BorderLayout.CENTER);

        JPanel topPanel = new JPanel(new GridLayout(2, 1, 10, 10));
        topPanel.add(inputPanel);
        topPanel.add(folderPanel);

        JSplitPane centerSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                new JScrollPane(downloadList),
                new JScrollPane(logArea));
        centerSplit.setResizeWeight(0.3);

        JPanel paddedPanel = new JPanel(new BorderLayout(10, 10));
        paddedPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        paddedPanel.add(topPanel, BorderLayout.NORTH);
        paddedPanel.add(centerSplit, BorderLayout.CENTER);

        frame.setContentPane(paddedPanel);
    }


    private void setupListeners() {
        chooseFolderButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
                downloadDirectory = chooser.getSelectedFile();
                settings.setDownloadDirectory(downloadDirectory);
                updateFolderLabel();
            }
        });

        downloadButton.addActionListener(e -> {
            String url = urlField.getText().trim();
            if (url.isEmpty() || downloadDirectory == null) {
                JOptionPane.showMessageDialog(frame, "No url selected or no directory chosen.");
                return;
            }
            logArea.setText("");
            downloader.download(url, downloadDirectory, logArea, downloadedTitle -> {
                history.add(downloadedTitle);
                downloadListModel.addElement(downloadedTitle);
            });
        });
    }

    private void updateFolderLabel() {
        folderLabel.setText(downloadDirectory != null ?
                "📂 " + downloadDirectory.getAbsolutePath() :
                "📂 No folder selected");
    }

    public void show() {
        frame.setVisible(true);
    }
}
