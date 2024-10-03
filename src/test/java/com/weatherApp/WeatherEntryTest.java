// WeatherEntryTest.java
package com.weatherApp;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WeatherEntryTest {

    @Test
    public void testSettersAndGetters() {
        WeatherEntry entry = new WeatherEntry();

        // Set values
        entry.setId("W001");
        entry.setName("Station Alpha");
        entry.setState("California");
        entry.setTimeZone("PST");
        entry.setLat(34.05);
        entry.setLon(-118.25);
        entry.setLocalDateTime("2024-04-27T10:00:00");
        entry.setLocalDateTimeFull("Saturday, April 27, 2024 10:00:00 AM");
        entry.setAirTemp(25.5);
        entry.setApparentT(27.0);
        entry.setCloud("Partly Cloudy");
        entry.setDewpt(15.0);
        entry.setPress(1015.2);
        entry.setRelHum(60);
        entry.setWindDir("NW");
        entry.setWindSpdKmh(15);
        entry.setWindSpdKt(8);
        entry.setTimestamp(1);
        entry.setLastUpdated(System.currentTimeMillis());

        // Assert values
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
        assertEquals(1, entry.getTimestamp());
        assertTrue(entry.getLastUpdated() > 0, "LastUpdated should be a positive timestamp");
    }

    @Test
    public void testDefaultValues() {
        WeatherEntry entry = new WeatherEntry();

        // Assert default values
        assertNull(entry.getId(), "Default id should be null");
        assertNull(entry.getName(), "Default name should be null");
        assertNull(entry.getState(), "Default state should be null");
        assertNull(entry.getTimeZone(), "Default timeZone should be null");
        assertEquals(0.0, entry.getLat(), "Default lat should be 0.0");
        assertEquals(0.0, entry.getLon(), "Default lon should be 0.0");
        assertNull(entry.getLocalDateTime(), "Default localDateTime should be null");
        assertNull(entry.getLocalDateTimeFull(), "Default localDateTimeFull should be null");
        assertEquals(0.0, entry.getAirTemp(), "Default airTemp should be 0.0");
        assertEquals(0.0, entry.getApparentT(), "Default apparentT should be 0.0");
        assertNull(entry.getCloud(), "Default cloud should be null");
        assertEquals(0.0, entry.getDewpt(), "Default dewpt should be 0.0");
        assertEquals(0.0, entry.getPress(), "Default press should be 0.0");
        assertEquals(0, entry.getRelHum(), "Default relHum should be 0");
        assertNull(entry.getWindDir(), "Default windDir should be null");
        assertEquals(0, entry.getWindSpdKmh(), "Default windSpdKmh should be 0");
        assertEquals(0, entry.getWindSpdKt(), "Default windSpdKt should be 0");
        assertEquals(0, entry.getTimestamp(), "Default timestamp should be 0");
        assertEquals(0L, entry.getLastUpdated(), "Default lastUpdated should be 0");
    }

    @Test
    public void testJsonSerialization() throws Exception {
        WeatherEntry entry = new WeatherEntry();
        entry.setId("W002");
        entry.setName("Station Beta");
        entry.setState("Nevada");
        entry.setTimeZone("PDT");
        entry.setLat(36.17);
        entry.setLon(-115.14);
        entry.setLocalDateTime("2024-05-15T14:30:00");
        entry.setLocalDateTimeFull("Wednesday, May 15, 2024 2:30:00 PM");
        entry.setAirTemp(30.0);
        entry.setApparentT(32.0);
        entry.setCloud("Sunny");
        entry.setDewpt(10.0);
        entry.setPress(1013.5);
        entry.setRelHum(40);
        entry.setWindDir("NE");
        entry.setWindSpdKmh(20);
        entry.setWindSpdKt(10);
        entry.setTimestamp(2);
        entry.setLastUpdated(System.currentTimeMillis());

        // Serialize to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(entry);

        // Deserialize back to object
        WeatherEntry deserializedEntry = objectMapper.readValue(jsonString, WeatherEntry.class);

        // Assert equality
        assertEquals(entry.getId(), deserializedEntry.getId());
        assertEquals(entry.getName(), deserializedEntry.getName());
        assertEquals(entry.getState(), deserializedEntry.getState());
        assertEquals(entry.getTimeZone(), deserializedEntry.getTimeZone());
        assertEquals(entry.getLat(), deserializedEntry.getLat());
        assertEquals(entry.getLon(), deserializedEntry.getLon());
        assertEquals(entry.getLocalDateTime(), deserializedEntry.getLocalDateTime());
        assertEquals(entry.getLocalDateTimeFull(), deserializedEntry.getLocalDateTimeFull());
        assertEquals(entry.getAirTemp(), deserializedEntry.getAirTemp());
        assertEquals(entry.getApparentT(), deserializedEntry.getApparentT());
        assertEquals(entry.getCloud(), deserializedEntry.getCloud());
        assertEquals(entry.getDewpt(), deserializedEntry.getDewpt());
        assertEquals(entry.getPress(), deserializedEntry.getPress());
        assertEquals(entry.getRelHum(), deserializedEntry.getRelHum());
        assertEquals(entry.getWindDir(), deserializedEntry.getWindDir());
        assertEquals(entry.getWindSpdKmh(), deserializedEntry.getWindSpdKmh());
        assertEquals(entry.getWindSpdKt(), deserializedEntry.getWindSpdKt());
        assertEquals(entry.getTimestamp(), deserializedEntry.getTimestamp());
        assertEquals(entry.getLastUpdated(), deserializedEntry.getLastUpdated());
    }
}
