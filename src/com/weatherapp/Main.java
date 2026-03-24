package com.weatherapp;

import com.weatherapp.ui.WeatherAppGUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Run GUI on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            WeatherAppGUI app = new WeatherAppGUI();
            app.setVisible(true);
        });
    }
}
