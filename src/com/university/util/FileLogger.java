package com.university.util;

import java.io.*;
import java.time.LocalDateTime;

public class FileLogger implements Persistable, Notifiable {

    private String logFilePath;

    public FileLogger(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    @Override
    public void logEvent(String event) {
        try (BufferedWriter writer = new BufferedWriter(
                new FileWriter(logFilePath, true))) {
            writer.write(LocalDateTime.now() + " | " + event);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Logging error: " + e.getMessage());
        }
    }

    @Override
    public void sendAlert(String message) {
        logEvent("ALERT | " + message);
        System.out.println("[ALERT] " + message);
    }

    @Override
    public void saveToFile(String filePath) throws IOException {
        logEvent("System saved to: " + filePath);
    }

    @Override
    public void loadFromFile(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new FileReader(filePath))) {
            String line;
            System.out.println("=== Log History ===");
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}