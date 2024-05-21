package org.lab5.lab_5.Utilities;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExportUtilities {
    static class LogEntry {
        private String timestamp;
        private String message;

        public LogEntry(String timestamp, String message) {
            this.timestamp = timestamp;
            this.message = message;
        }

        // Getters (for Gson serialization)
        public String getTimestamp() {
            return timestamp;
        }
        public String getMessage() {
            return message;
        }
    }
    public static void exportJSON(String logs) {
        String fileName = "serverLogs.json";
        List<LogEntry> logEntries = new ArrayList<>();

        String[] lines = logs.split("\\n");
        for (String line : lines) {
            String[] oneLogInfo = line.split("::");
            logEntries.add(new LogEntry(oneLogInfo[0], oneLogInfo[1]));
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String jsonOutput = gson.toJson(logEntries);

        try (FileWriter writer = new FileWriter(fileName)) {
            // Write JSON string to the output file
            writer.write(jsonOutput);
        } catch (IOException e) {
            System.err.println("Error writing JSON data to " + fileName + ": " + e.getMessage());
        }
    }
    public static void exportCSV(String logs) {
        String fileName = "serverLogs.csv";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(logs);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void exportTXT(String logs) {
        String fileName = "serverLogs.txt";
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            writer.write(logs);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
