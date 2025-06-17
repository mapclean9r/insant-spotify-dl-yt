package org.example;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.util.function.Consumer;

import java.nio.file.*;

public class YouTubeDownloader {

    public void download(String url, File outputDirectory, JTextArea outputArea, Consumer<String> onComplete) {
        File tmpDir = new File(outputDirectory, "tmp");
        if (!tmpDir.exists()){
            tmpDir.mkdirs();
        }

        ProcessBuilder builder = new ProcessBuilder(
                "yt-dlp",
                "-x",
                "--audio-format", "mp3",
                "-o", tmpDir.getAbsolutePath() + "/%(title)s.%(ext)s",
                url
        );

        builder.redirectErrorStream(true);

        new Thread(() -> {
            try {
                Process process = builder.start();
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

                String line;
                while ((line = reader.readLine()) != null) {
                    String finalLine = line;
                    SwingUtilities.invokeLater(() -> {
                        outputArea.append(finalLine + "\n");
                        outputArea.setForeground(Color.BLACK);
                    });
                }

                int exitCode = process.waitFor();

                if (exitCode == 0) {
                    File[] mp3Files = tmpDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".mp3"));
                    if (mp3Files != null) {
                        for (File tmpMp3 : mp3Files) {
                            File targetFile = new File(outputDirectory, tmpMp3.getName());

                            try {
                                Files.move(tmpMp3.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                                File tmpSwap = new File(outputDirectory, targetFile.getName().replace(".mp3", "_tmp.mp3"));
                                Files.move(targetFile.toPath(), tmpSwap.toPath());
                                Files.move(tmpSwap.toPath(), targetFile.toPath());

                                onComplete.accept(targetFile.getName().replaceFirst("\\.mp3$", ""));

                            } catch (IOException e) {
                                e.printStackTrace();
                                outputArea.append("❌ ERROR moving: " + tmpMp3.getName() + "\n");
                            }
                        }
                    } else {
                        outputArea.append("❌ TMP empty\n");
                    }

                    deleteDirectoryRecursively(tmpDir);
                } else {
                    outputArea.append("❌ Download failed.\n");
                }

                SwingUtilities.invokeLater(() -> outputArea.append("\n✅ Complete\n"));

            } catch (IOException | InterruptedException ex) {
                ex.printStackTrace();
                SwingUtilities.invokeLater(() ->
                        outputArea.append("❌ Error: " + ex.getMessage() + "\n")
                );
            }
        }).start();
    }

    private void deleteDirectoryRecursively(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        deleteDirectoryRecursively(file);
                    } else {
                        file.delete();
                    }
                }
            }
            dir.delete();
        }
    }
}
