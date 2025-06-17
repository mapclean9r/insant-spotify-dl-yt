package org.example;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DownloadHistory {
    private static final String HISTORY_FILE = "downloads.log";
    private final List<String> history = new ArrayList<>();

    public DownloadHistory() {
        load();
    }

    private void load() {
        File file = new File(HISTORY_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    history.add(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void add(String entry) {
        history.add(entry);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_FILE, true))) {
            writer.write(entry);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> getHistory() {
        return history;
    }
}
