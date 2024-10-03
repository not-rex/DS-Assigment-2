package com.weatherApp;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.weatherApp.WeatherDataStore;
import com.weatherApp.WeatherEntry;

import org.junit.jupiter.api.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class WeatherDataStoreTest {

    private WeatherDataStore dataStore;
    private Path tempFilePath;
    private ObjectMapper objectMapper;

    @BeforeAll
    public void setup() throws IOException {
        objectMapper = new ObjectMapper();
        // Create a temporary file for testing
        tempFilePath = Files.createTempFile("weather_data_store_test", ".json");
        dataStore = new WeatherDataStore(tempFilePath.toString());
    }

    @AfterAll
    public void teardown() throws IOException {
        // Delete the temporary file after all tests
        Files.deleteIfExists(tempFilePath);
    }

    @BeforeEach
    public void beforeEachTest() throws IOException {
        // Clear data before each test to ensure isolation
        dataStore.clearData();
        // Ensure the temporary file is empty
        Files.writeString(tempFilePath, "");
    }

    // Helper method to create a WeatherEntry object.
    private WeatherEntry createWeatherEntry(String id) {
        WeatherEntry entry = new WeatherEntry();
        entry.setId(id);
        entry.setName("Test Station " + id);
        entry.setState("Test State");
        entry.setTimeZone("Test TimeZone");
        entry.setLat(12.34);
        entry.setLon(56.78);
        entry.setLocalDateTime("2024-10-03T12:00:00");
        entry.setLocalDateTimeFull("Thursday, October 3, 2024 12:00:00 PM");
        entry.setAirTemp(25.0);
        entry.setApparentT(27.0);
        entry.setCloud("Sunny");
        entry.setDewpt(15.0);
        entry.setPress(1010.0);
        entry.setRelHum(50);
        entry.setWindDir("NE");
        entry.setWindSpdKmh(10);
        entry.setWindSpdKt(5);
        entry.setTimestamp(1);
        entry.setLastUpdated(System.currentTimeMillis());
        return entry;
    }

    // Test loading persisted data when the file is empty.
    @Test
    public void testLoadPersistedDataEmptyFile() {
        dataStore.loadPersistedData();
        List<WeatherEntry> data = dataStore.getData();
        assertNotNull(data, "Data list should not be null");
        assertTrue(data.isEmpty(), "Data list should be empty when file is empty");
    }

    // Test loading persisted data from a non-empty file.
    @Test
    public void testLoadPersistedDataNonEmptyFile() throws IOException {
        // Create sample data
        WeatherEntry entry1 = createWeatherEntry("W100");
        WeatherEntry entry2 = createWeatherEntry("W101");
        List<WeatherEntry> entries = List.of(entry1, entry2);
        // Write to temporary file
        objectMapper.writeValue(tempFilePath.toFile(), entries);

        // Load persisted data
        dataStore.loadPersistedData();
        List<WeatherEntry> loadedData = dataStore.getData();

        assertEquals(2, loadedData.size(), "Data list should contain 2 entries");
        assertTrue(loadedData.contains(entry1), "Data list should contain entry1");
        assertTrue(loadedData.contains(entry2), "Data list should contain entry2");
    }

    // Test loading persisted data from a malformed JSON file.
    @Test
    public void testLoadPersistedDataMalformedJson() throws IOException {
        // Write malformed JSON to temporary file
        Files.writeString(tempFilePath, "This is not valid JSON");

        // Capture error
        PrintStream originalErr = System.err;
        ByteArrayOutputStream errContent = new ByteArrayOutputStream();
        System.setErr(new PrintStream(errContent));

        // Load persisted data
        dataStore.loadPersistedData();
        List<WeatherEntry> loadedData = dataStore.getData();

        // Restore original System.err
        System.setErr(originalErr);

        assertTrue(loadedData.isEmpty(), "Data list should be empty when JSON is malformed");
        String errorOutput = errContent.toString();
        assertTrue(errorOutput.contains("Failed to load persisted data"), "Should print error message for malformed JSON");
    }

    // Test persisting data to the JSON file.
    @Test
    public void testPersistData() throws IOException {
        // Add entries to dataStore
        WeatherEntry entry1 = createWeatherEntry("W100");
        WeatherEntry entry2 = createWeatherEntry("W101");
        dataStore.saveData(entry1, entry1.getTimestamp());
        dataStore.saveData(entry2, entry2.getTimestamp());

        // Persist data
        dataStore.persistData();

        // Read from file and verify
        List<WeatherEntry> persistedEntries = objectMapper.readValue(
                tempFilePath.toFile(),
                new TypeReference<List<WeatherEntry>>() {}
        );

        assertEquals(2, persistedEntries.size(), "Persisted data should contain 2 entries");
        assertTrue(persistedEntries.contains(entry1), "Persisted data should contain entry1");
        assertTrue(persistedEntries.contains(entry2), "Persisted data should contain entry2");
    }

    // Test saving a new WeatherEntry.
    @Test
    public void testSaveDataNewEntry() {
        WeatherEntry newEntry = createWeatherEntry("W100");
        assertTrue(dataStore.isNewEntry("W100"), "Entry W100 should be new");

        dataStore.saveData(newEntry, newEntry.getTimestamp());

        assertFalse(dataStore.isNewEntry("W100"), "Entry W100 should no longer be new");
        List<WeatherEntry> data = dataStore.getData();
        assertEquals(1, data.size(), "Data list should contain 1 entry");
        assertEquals(newEntry, data.get(0), "Data list should contain the new entry");
    }

    // Test updating an existing WeatherEntry.
    @Test
    public void testSaveDataUpdateEntry() {
        // Add initial entry with timestamp 1
        WeatherEntry initialEntry = createWeatherEntry("W100");
        initialEntry.setTimestamp(1);
        dataStore.saveData(initialEntry, initialEntry.getTimestamp());

        // Modify some fields and set timestamp to 3
        WeatherEntry updatedEntry = createWeatherEntry("W100");
        updatedEntry.setAirTemp(30.0);
        updatedEntry.setCloud("Cloudy");

        // Pass lamportTime as 3 instead of updatedEntry.getTimestamp() + 1
        dataStore.saveData(updatedEntry, 3);

        // Retrieve data and verify updates
        List<WeatherEntry> data = dataStore.getData();
        assertEquals(1, data.size(), "Data list should contain 1 entry");
        WeatherEntry retrievedEntry = data.get(0);
        assertEquals("Cloudy", retrievedEntry.getCloud(), "Cloud field should be updated");
        assertEquals(30.0, retrievedEntry.getAirTemp(), "Air Temperature should be updated");
        assertEquals(3, retrievedEntry.getTimestamp(), "Timestamp should be updated to 3");
    }


    // Test retrieving all WeatherEntry data.
    @Test
    public void testGetData() {
        WeatherEntry entry1 = createWeatherEntry("W100");
        WeatherEntry entry2 = createWeatherEntry("W101");
        dataStore.saveData(entry1, entry1.getTimestamp());
        dataStore.saveData(entry2, entry2.getTimestamp());

        List<WeatherEntry> data = dataStore.getData();
        assertEquals(2, data.size(), "Data list should contain 2 entries");
        assertTrue(data.contains(entry1), "Data list should contain entry1");
        assertTrue(data.contains(entry2), "Data list should contain entry2");
    }

    // Test checking if an entry is new.
    @Test
    public void testIsNewEntry() {
        assertTrue(dataStore.isNewEntry("W100"), "Entry W100 should be new");

        WeatherEntry entry = createWeatherEntry("W100");
        dataStore.saveData(entry, entry.getTimestamp());

        assertFalse(dataStore.isNewEntry("W100"), "Entry W100 should no longer be new");
    }

    //Test clearing all data from the data store.
    @Test
    public void testClearData() {
        WeatherEntry entry1 = createWeatherEntry("W100");
        WeatherEntry entry2 = createWeatherEntry("W101");
        dataStore.saveData(entry1, entry1.getTimestamp());
        dataStore.saveData(entry2, entry2.getTimestamp());

        List<WeatherEntry> dataBeforeClear = dataStore.getData();
        assertEquals(2, dataBeforeClear.size(), "Data list should contain 2 entries before clearing");

        dataStore.clearData();

        List<WeatherEntry> dataAfterClear = dataStore.getData();
        assertTrue(dataAfterClear.isEmpty(), "Data list should be empty after clearing");
    }

    // Test expiring old data based on lastUpdated timestamp.
    @Test
    public void testExpireOldData() {
        long currentTime = System.currentTimeMillis();

        // Create entries
        WeatherEntry recentEntry = createWeatherEntry("W100");
        recentEntry.setLastUpdated(currentTime - (23 * 60 * 60 * 1000)); // 23 hours ago

        WeatherEntry oldEntry = createWeatherEntry("W101");
        oldEntry.setLastUpdated(currentTime - (25 * 60 * 60 * 1000)); // 25 hours ago

        WeatherEntry veryOldEntry = createWeatherEntry("W102");
        veryOldEntry.setLastUpdated(currentTime - (48 * 60 * 60 * 1000)); // 48 hours ago

        // Entries to dataStore
        dataStore.saveData(recentEntry, recentEntry.getTimestamp());
        dataStore.saveData(oldEntry, oldEntry.getTimestamp());
        dataStore.saveData(veryOldEntry, veryOldEntry.getTimestamp());

        // Verify entries are present before expiration
        List<WeatherEntry> dataBeforeExpiration = dataStore.getData();
        assertEquals(3, dataBeforeExpiration.size(), "Data list should contain 3 entries before expiration");

        // Call expireOldData()
        dataStore.expireOldData();

        // Retrieve data after expiration
        List<WeatherEntry> dataAfterExpiration = dataStore.getData();

        // Verify that only the recentEntry remains
        assertEquals(1, dataAfterExpiration.size(), "Data list should contain 1 entry after expiration");
        assertTrue(dataAfterExpiration.contains(recentEntry), "Data list should contain only the recent entry");
        assertFalse(dataAfterExpiration.contains(oldEntry), "Data list should not contain the old entry");
        assertFalse(dataAfterExpiration.contains(veryOldEntry), "Data list should not contain the very old entry");
    }

    // Test persisting data after saving entries.
    @Test
    public void testPersistDataAfterSave() throws IOException {
        // Add entries to dataStore
        WeatherEntry entry1 = createWeatherEntry("W100");
        WeatherEntry entry2 = createWeatherEntry("W101");
        dataStore.saveData(entry1, entry1.getTimestamp());
        dataStore.saveData(entry2, entry2.getTimestamp());

        // Persist data
        dataStore.persistData();

        // Read from file and verify
        List<WeatherEntry> persistedEntries = objectMapper.readValue(
                tempFilePath.toFile(),
                new TypeReference<List<WeatherEntry>>() {}
        );

        assertEquals(2, persistedEntries.size(), "Persisted data should contain 2 entries");
        assertTrue(persistedEntries.contains(entry1), "Persisted data should contain entry1");
        assertTrue(persistedEntries.contains(entry2), "Persisted data should contain entry2");
    }

    // Test saving data when the entry ID does not exist.
    @Test
    public void testSaveDataWithNonExistingId() {
        WeatherEntry entry = createWeatherEntry("W102");
        assertTrue(dataStore.isNewEntry("W102"), "Entry W102 should be new");

        dataStore.saveData(entry, entry.getTimestamp());

        assertFalse(dataStore.isNewEntry("W102"), "Entry W102 should no longer be new");
        List<WeatherEntry> data = dataStore.getData();
        assertEquals(1, data.size(), "Data list should contain 1 entry");
        assertEquals(entry, data.get(0), "Data list should contain the new entry");
    }
}
