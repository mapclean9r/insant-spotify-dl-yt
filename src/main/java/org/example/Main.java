package org.example;

import javax.swing.*;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            DownloaderUI ui = new DownloaderUI();
            ui.show();
        });
    }
}
