package com.weatherapp.ui;

import com.weatherapp.model.ForecastData;
import com.weatherapp.model.WeatherData;
import com.weatherapp.service.HistoryManager;
import com.weatherapp.service.WeatherService;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class WeatherAppGUI extends JFrame {
    private final WeatherService weatherService;
    private final HistoryManager historyManager;

    private boolean isMetric = true; // true = Metric (Celsius/mps), false = Imperial (Fahrenheit/mph)
    private WeatherData currentWeatherData;

    private DynamicBackgroundPanel mainPanel;
    private JTextField searchField;
    private JButton searchButton;
    private JButton unitToggleButton;
    private JButton historyButton;

    // Current Weather Components
    private JLabel cityNameLabel;
    private JLabel temperatureLabel;
    private JLabel conditionLabel;
    private JLabel humidityLabel;
    private JLabel windLabel;
    private JLabel iconLabel;

    // Forecast Panel
    private JPanel forecastPanel;

    public WeatherAppGUI() {
        this.weatherService = new WeatherService();
        this.historyManager = new HistoryManager();

        setTitle("Weather Information App");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        mainPanel = new DynamicBackgroundPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(15, 15, 15, 15));

        // TOP BAR: Search Field & Buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        topPanel.setOpaque(false);

        searchField = new JTextField(20);
        searchField.setFont(new Font("SansSerif", Font.PLAIN, 18));
        searchField.addActionListener(e -> performSearch());

        JPopupMenu suggestionMenu = new JPopupMenu();
        suggestionMenu.setFocusable(false);
        Timer debounceTimer = new Timer(500, e -> {
            String q = searchField.getText().trim();
            if (q.length() < 2) {
                suggestionMenu.setVisible(false);
                return;
            }
            new SwingWorker<List<String>, Void>() {
                @Override
                protected List<String> doInBackground() throws Exception {
                    return weatherService.getSuggestions(q);
                }
                @Override
                protected void done() {
                    try {
                        List<String> list = get();
                        suggestionMenu.removeAll();
                        if (list != null && !list.isEmpty()) {
                            for (String s : list) {
                                JMenuItem item = new JMenuItem(s);
                                item.addActionListener(ev -> {
                                    searchField.setText(s);
                                    suggestionMenu.setVisible(false);
                                    performSearch();
                                });
                                suggestionMenu.add(item);
                            }
                            suggestionMenu.pack();
                            suggestionMenu.show(searchField, 0, searchField.getHeight());
                            searchField.requestFocus();
                        } else {
                            suggestionMenu.setVisible(false);
                        }
                    } catch (Exception ignored) {}
                }
            }.execute();
        });
        debounceTimer.setRepeats(false);

        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { debounceTimer.restart(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { debounceTimer.restart(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { debounceTimer.restart(); }
        });

        searchButton = new JButton("Search");
        searchButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        searchButton.addActionListener(e -> performSearch());

        unitToggleButton = new JButton("Switch to Fahrenheit \u00B0F");
        unitToggleButton.addActionListener(e -> toggleUnits());

        historyButton = new JButton("View History");
        historyButton.addActionListener(e -> showHistoryDialog());

        topPanel.add(new JLabel("Location:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(unitToggleButton);
        topPanel.add(historyButton);

        // MIDDLE: Current Weather Display
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);

        cityNameLabel = new JLabel("Enter a city to get weather Data", SwingConstants.CENTER);
        cityNameLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        cityNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        cityNameLabel.setForeground(Color.WHITE);

        iconLabel = new JLabel("", SwingConstants.CENTER);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        temperatureLabel = new JLabel("-- \u00B0C", SwingConstants.CENTER);
        temperatureLabel.setFont(new Font("SansSerif", Font.BOLD, 64));
        temperatureLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        temperatureLabel.setForeground(Color.WHITE);

        conditionLabel = new JLabel("--", SwingConstants.CENTER);
        conditionLabel.setFont(new Font("SansSerif", Font.PLAIN, 24));
        conditionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        conditionLabel.setForeground(Color.WHITE);

        JPanel detailsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 10));
        detailsPanel.setOpaque(false);
        humidityLabel = new JLabel("Humidity: --%");
        humidityLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        humidityLabel.setForeground(Color.WHITE);

        windLabel = new JLabel("Wind: -- m/s");
        windLabel.setFont(new Font("SansSerif", Font.PLAIN, 18));
        windLabel.setForeground(Color.WHITE);

        detailsPanel.add(humidityLabel);
        detailsPanel.add(windLabel);

        centerPanel.add(Box.createVerticalStrut(20));
        centerPanel.add(cityNameLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(iconLabel);
        centerPanel.add(temperatureLabel);
        centerPanel.add(conditionLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(detailsPanel);

        // BOTTOM: Forecast Panel
        forecastPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        forecastPanel.setOpaque(false);
        forecastPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.WHITE), "Upcoming Forecast"));
        ((javax.swing.border.TitledBorder)forecastPanel.getBorder()).setTitleColor(Color.WHITE);

        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(forecastPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    private void performSearch() {
        String query = searchField.getText().trim();
        if (query.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a location.", "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Fetch Data Asynchronously so UI doesn't freeze
        searchButton.setEnabled(false);
        searchButton.setText("Locating...");

        new SwingWorker<Void, Void>() {
            private String error = null;
            private List<ForecastData> forecastList;

            @Override
            protected Void doInBackground() {
                try {
                    currentWeatherData = weatherService.getCurrentWeather(query);
                    forecastList = weatherService.getForecast(query);
                    historyManager.addSearchEntry(query + " (" + currentWeatherData.getCityName() + ", " + currentWeatherData.getTemperature() + "K)");
                } catch (Exception ex) {
                    error = ex.getMessage();
                }
                return null;
            }

            @Override
            protected void done() {
                searchButton.setEnabled(true);
                searchButton.setText("Search");

                if (error != null) {
                    JOptionPane.showMessageDialog(WeatherAppGUI.this, "Error fetching data: \n" + error, "API Error", JOptionPane.ERROR_MESSAGE);
                } else {
                    updateUIWithCurrentData();
                    updateForecastPanel(forecastList);
                }
            }
        }.execute();
    }

    private void updateUIWithCurrentData() {
        if (currentWeatherData == null) return;

        cityNameLabel.setText(currentWeatherData.getCityName());
        conditionLabel.setText(currentWeatherData.getCondition());

        // Dynamic background update
        long now = System.currentTimeMillis() / 1000;
        // Adjust background based on sunrise and sunset
        boolean isDaytime = (now >= currentWeatherData.getSunrise() && now <= currentWeatherData.getSunset());
        mainPanel.setDaytime(isDaytime);
        mainPanel.repaint();

        // Download icon safely in another thread to avoid lag
        new SwingWorker<ImageIcon, Void>() {
            @Override
            protected ImageIcon doInBackground() throws Exception {
                String iconCode = currentWeatherData.getIconCode();
                URL url = URI.create("https://openweathermap.org/img/wn/" + iconCode + "@4x.png").toURL();
                URLConnection conn = url.openConnection();
                conn.setRequestProperty("User-Agent", "Mozilla/5.0");
                try (InputStream stream = conn.getInputStream()) {
                    Image img = ImageIO.read(stream);
                    if (img != null) {
                        return new ImageIcon(img);
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    ImageIcon icon = get();
                    if (icon != null) {
                        iconLabel.setIcon(icon);
                    }
                } catch (Exception ignored) { }
            }
        }.execute();

        formatAndDisplayValues();
    }

    private void updateForecastPanel(List<ForecastData> forecastList) {
        forecastPanel.removeAll();
        if (forecastList != null) {
            for (ForecastData fd : forecastList) {
                JPanel fCard = new JPanel();
                fCard.setLayout(new BoxLayout(fCard, BoxLayout.Y_AXIS));
                fCard.setOpaque(false);

                // Date/Time
                String[] dtParts = fd.getDatetime().split(" ");
                String timeStr = dtParts.length > 1 ? dtParts[1].substring(0, 5) : dtParts[0];
                JLabel timeLbl = new JLabel(timeStr);
                timeLbl.setForeground(Color.WHITE);
                timeLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

                // Icon placeholder
                JLabel fIconLbl = new JLabel("...");
                fIconLbl.setAlignmentX(Component.CENTER_ALIGNMENT);

                new SwingWorker<ImageIcon, Void>() {
                    @Override
                    protected ImageIcon doInBackground() throws Exception {
                        URL url = URI.create("https://openweathermap.org/img/wn/" + fd.getIconCode() + "@2x.png").toURL();
                        URLConnection conn = url.openConnection();
                        try (InputStream stream = conn.getInputStream()) {
                            Image img = ImageIO.read(stream);
                            if (img != null) {
                                return new ImageIcon(img);
                            }
                        }
                        return null;
                    }
                    @Override
                    protected void done() {
                        try {
                            ImageIcon ic = get();
                            if (ic != null) fIconLbl.setIcon(ic);
                            else fIconLbl.setText(fd.getCondition());
                        } catch(Exception ignored){}
                    }
                }.execute();

                double displayTemp = isMetric ? kelvinToCelsius(fd.getTemperature()) : kelvinToFahrenheit(fd.getTemperature());
                String tString = String.format("%.0f \u00B0%s", displayTemp, isMetric ? "C" : "F");

                JLabel tLbl = new JLabel(tString);
                tLbl.setForeground(Color.WHITE);
                tLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
                tLbl.setFont(new Font("SansSerif", Font.BOLD, 14));

                fCard.add(timeLbl);
                fCard.add(Box.createVerticalStrut(5));
                fCard.add(fIconLbl);
                fCard.add(Box.createVerticalStrut(5));
                fCard.add(tLbl);

                forecastPanel.add(fCard);
            }
        }
        forecastPanel.revalidate();
        forecastPanel.repaint();
    }

    private void toggleUnits() {
        isMetric = !isMetric;
        unitToggleButton.setText(isMetric ? "Switch to Fahrenheit \u00B0F" : "Switch to Celsius \u00B0C");
        formatAndDisplayValues();
    }

    private void formatAndDisplayValues() {
        if (currentWeatherData == null) return;

        double tempK = currentWeatherData.getTemperature();
        double wSpeedMPS = currentWeatherData.getWindSpeed();

        if (isMetric) {
            double tempC = kelvinToCelsius(tempK);
            temperatureLabel.setText(String.format("%.1f \u00B0C", tempC));
            windLabel.setText(String.format("Wind: %.1f m/s", wSpeedMPS));
        } else {
            double tempF = kelvinToFahrenheit(tempK);
            double wSpeedMPH = wSpeedMPS * 2.23694; // m/s to mph
            temperatureLabel.setText(String.format("%.1f \u00B0F", tempF));
            windLabel.setText(String.format("Wind: %.1f mph", wSpeedMPH));
        }

        humidityLabel.setText(String.format("Humidity: %.0f%%", currentWeatherData.getHumidity()));
    }

    private void showHistoryDialog() {
        List<String> list = historyManager.getHistory();
        JTextArea area = new JTextArea(15, 30);
        area.setEditable(false);
        if (list.isEmpty()) {
            area.setText("No history available.");
        } else {
            for (String str : list) {
                area.append(str + "\n");
            }
        }
        
        JScrollPane scrollPane = new JScrollPane(area);
        JOptionPane.showMessageDialog(this, scrollPane, "Search History", JOptionPane.INFORMATION_MESSAGE);
    }

    // Converters
    private double kelvinToCelsius(double k) {
        return k - 273.15;
    }

    private double kelvinToFahrenheit(double k) {
        return (k - 273.15) * 9/5 + 32;
    }

    /**
     * A custom panel that draws a dynamic gradient background representing day or night.
     */
    private static class DynamicBackgroundPanel extends JPanel {
        private boolean isDaytime = true;

        public void setDaytime(boolean daytime) {
            this.isDaytime = daytime;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();

            Color topColor;
            Color bottomColor;

            if (isDaytime) {
                topColor = new Color(135, 206, 235);   // Sky Blue
                bottomColor = new Color(25, 25, 112);  // Midnight Blue
            } else {
                topColor = new Color(20, 20, 50);      // Very Dark Blue
                bottomColor = new Color(75, 0, 130);   // Indigo / Night Purple
            }

            GradientPaint gp = new GradientPaint(0, 0, topColor, 0, height, bottomColor);
            g2d.setPaint(gp);
            g2d.fillRect(0, 0, width, height);
        }
    }
}
