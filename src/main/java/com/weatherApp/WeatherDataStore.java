package com.weatherApp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.ArrayList;

public class WeatherDataStore {
    private final String filePath;
    private final ConcurrentHashMap<String, WeatherEntry> dataMap = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public WeatherDataStore(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads persisted data from the JSON file into the data map.
     */
    public void loadPersistedData() {
        File file = new File(filePath);
        if (!file.exists()) {
            // If file doesn't exist, create an empty file
            try {
                file.createNewFile();
                objectMapper.writeValue(file, new ArrayList<WeatherEntry>());
            } catch (IOException e) {
                System.err.println("Error creating new data file: " + e.getMessage());
            }
            return;
        }

        try {
            List<WeatherEntry> entries = objectMapper.readValue(file, new TypeReference<List<WeatherEntry>>() {});
            for (WeatherEntry entry : entries) {
                dataMap.put(entry.getId(), entry);
            }
        } catch (IOException e) {
            System.err.println("Error loading persisted data: " + e.getMessage());
        }
    }

    /**
     * Persists current data map to the JSON file.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void persistData() throws IOException {
        List<WeatherEntry> entries = List.copyOf(dataMap.values());
        objectMapper.writeValue(new File(filePath), entries);
    }

    /**
     * Saves or updates weather data in the data map.
     *
     * @param weatherEntry The WeatherEntry object to be saved.
     * @param timestamp    The Lamport timestamp associated with the data.
     */
    public void saveData(WeatherEntry weatherEntry, int timestamp) {
        weatherEntry.setTimestamp(timestamp);
        weatherEntry.setLastUpdated(System.currentTimeMillis());
        dataMap.put(weatherEntry.getId(), weatherEntry);
    }

    /**
     * Retrieves all current weather data as a list.
     *
     * @return List of WeatherEntry objects.
     */
    public List<WeatherEntry> getData() {
        return List.copyOf(dataMap.values());
    }

    /**
     * Expires data from content servers that haven't communicated within the last 30 seconds.
     */
    public void expireOldData() {
        long now = System.currentTimeMillis();
        dataMap.values().removeIf(entry -> (now - entry.getLastUpdated()) > 30000);
    }

    /**
     * Checks if the entry is newly added.
     *
     * @param id The ID of the WeatherEntry.
     * @return true if it's a new entry, false otherwise.
     */
    public boolean isNewEntry(String id) {
        // Implement logic to determine if the entry was newly added
        // For simplicity, assume all PUTs are updates and return false
        // Alternatively, maintain a separate set of new entries
        return false;
    }
}
