package org.example;

import java.io.*;
import java.util.Properties;

public class UserSettings {
    private static final String SETTINGS_FILE = "settings.properties";
    private static final String KEY_DOWNLOAD_DIR = "download_dir";

    private final Properties props = new Properties();

    public UserSettings() {
        load();
    }

    private void load() {
        File file = new File(SETTINGS_FILE);
        if (file.exists()) {
            try (FileReader reader = new FileReader(file)) {
                props.load(reader);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void save() {
        try (FileWriter writer = new FileWriter(SETTINGS_FILE)) {
            props.store(writer, "User Settings");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public File getDownloadDirectory() {
        String path = props.getProperty(KEY_DOWNLOAD_DIR);
        return path != null ? new File(path) : null;
    }

    public void setDownloadDirectory(File dir) {
        if (dir != null) {
            props.setProperty(KEY_DOWNLOAD_DIR, dir.getAbsolutePath());
            save();
        }
    }
}
