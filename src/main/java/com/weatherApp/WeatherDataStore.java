package com.weatherApp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WeatherDataStore {
    private List<WeatherEntry> dataList;
    private String filePath;
    private ObjectMapper objectMapper;

    public WeatherDataStore(String filePath) {
        this.filePath = filePath;
        this.objectMapper = new ObjectMapper();
        this.dataList = new CopyOnWriteArrayList<>();
    }

    /**
     * Loads persisted data from the JSON file.
     */
    public void loadPersistedData() {
        File file = new File(filePath);
        if (!file.exists()) {
            return; // No data to load
        }
        try {
            dataList = objectMapper.readValue(file, new TypeReference<List<WeatherEntry>>() {});
        } catch (IOException e) {
            System.err.println("Failed to load persisted data: " + e.getMessage());
        }
    }

    /**
     * Persists current data to the JSON file.
     *
     * @throws IOException If an I/O error occurs.
     */
    public void persistData() throws IOException {
        objectMapper.writeValue(new File(filePath), dataList);
    }

    /**
     * Saves a WeatherEntry to the data store.
     *
     * @param entry        The WeatherEntry to save.
     * @param lamportTime  The Lamport timestamp.
     */
    public void saveData(WeatherEntry entry, int lamportTime) {
        // Check if entry with same ID exists
        boolean isNew = true;
        for (WeatherEntry existingEntry : dataList) {
            if (existingEntry.getId().equals(entry.getId())) {
                // Update existing entry
                existingEntry.setName(entry.getName());
                existingEntry.setState(entry.getState());
                existingEntry.setTimeZone(entry.getTimeZone());
                existingEntry.setLat(entry.getLat());
                existingEntry.setLon(entry.getLon());
                existingEntry.setLocalDateTime(entry.getLocalDateTime());
                existingEntry.setLocalDateTimeFull(entry.getLocalDateTimeFull());
                existingEntry.setAirTemp(entry.getAirTemp());
                existingEntry.setApparentT(entry.getApparentT());
                existingEntry.setCloud(entry.getCloud());
                existingEntry.setDewpt(entry.getDewpt());
                existingEntry.setPress(entry.getPress());
                existingEntry.setRelHum(entry.getRelHum());
                existingEntry.setWindDir(entry.getWindDir());
                existingEntry.setWindSpdKmh(entry.getWindSpdKmh());
                existingEntry.setWindSpdKt(entry.getWindSpdKt());
                existingEntry.setTimestamp(lamportTime);
                existingEntry.setLastUpdated(entry.getLastUpdated());
                isNew = false;
                break;
            }
        }
        if (isNew) {
            dataList.add(entry);
        }
    }

    /**
     * Retrieves all WeatherEntry data.
     *
     * @return List of WeatherEntry objects.
     */
    public List<WeatherEntry> getData() {
        return dataList;
    }

    /**
     * Determines if the given ID corresponds to a new entry.
     *
     * @param id The ID to check.
     * @return True if new, false otherwise.
     */
    public boolean isNewEntry(String id) {
        for (WeatherEntry entry : dataList) {
            if (entry.getId().equals(id)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Clears all data from the data store.
     */
    public void clearData() {
        dataList.clear();
    }

    /**
     * Expires old data based on predefined criteria.
     */
    public void expireOldData() {
        // Implement your data expiration logic here
    }
}
