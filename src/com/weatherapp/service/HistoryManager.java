package com.weatherapp.service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles saving and retrieving the search history of the user.
 */
public class HistoryManager {

    private static final String HISTORY_FILE = "search_history.txt";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public void addSearchEntry(String location) {
        try (FileWriter fw = new FileWriter(HISTORY_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            String timestamp = LocalDateTime.now().format(formatter);
            out.println(timestamp + " - " + location);
        } catch (IOException e) {
            System.err.println("Could not save to history file: " + e.getMessage());
        }
    }

    public List<String> getHistory() {
        List<String> history = new ArrayList<>();
        File file = new File(HISTORY_FILE);
        if (!file.exists()) {
            return history;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    history.add(line);
                }
            }
        } catch (IOException e) {
            System.err.println("Could not read history file: " + e.getMessage());
        }

        return history;
    }
}
