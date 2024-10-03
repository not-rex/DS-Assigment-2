package com.weatherApp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.weatherApp.ContentServer;
import com.weatherApp.WeatherEntry;

import java.io.IOException;

public class ContentServerTest {

    @Test
    public void testReadFileWithValidData() {
        String filePath = "src/test/resources/valid_weather.txt";
        try {
            WeatherEntry entry = ContentServer.readFile(filePath);
            assertNotNull(entry, "WeatherEntry should not be null");
            assertEquals("W001", entry.getId());
            assertEquals("Station Alpha", entry.getName());
            assertEquals("California", entry.getState());
            assertEquals("PST", entry.getTimeZone());
            assertEquals(34.05, entry.getLat());
            assertEquals(-118.25, entry.getLon());
            assertEquals("2024-04-27T10:00:00", entry.getLocalDateTime());
            assertEquals("Saturday, April 27, 2024 10:00:00 AM", entry.getLocalDateTimeFull());
            assertEquals(25.5, entry.getAirTemp());
            assertEquals(27.0, entry.getApparentT());
            assertEquals("Partly Cloudy", entry.getCloud());
            assertEquals(15.0, entry.getDewpt());
            assertEquals(1015.2, entry.getPress());
            assertEquals(60, entry.getRelHum());
            assertEquals("NW", entry.getWindDir());
            assertEquals(15, entry.getWindSpdKmh());
            assertEquals(8, entry.getWindSpdKt());
        } catch (IOException e) {
            fail("IOException was thrown for valid data: " + e.getMessage());
        }
    }

    @Test
    public void testReadFileMissingId() {
        String filePath = "src/test/resources/missing_id_weather.txt"; // Ensure this file exists
        Exception exception = assertThrows(IOException.class, () -> {
            ContentServer.readFile(filePath);
        }, "Expected IOException for missing 'id' field.");
        
        String expectedMessage = "Missing 'id' in weather data.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Exception message should indicate missing 'id'.");
    }

    @Test
    public void testReadFileWithInvalidFormat() {
        String filePath = "src/test/resources/invalid_format_weather.txt"; // Ensure this file exists
        Exception exception = assertThrows(IOException.class, () -> {
            ContentServer.readFile(filePath);
        }, "Expected IOException for invalid format.");

        String expectedMessage = "Missing 'id' in weather data.";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage), "Exception message should indicate missing 'id'.");
    }

}