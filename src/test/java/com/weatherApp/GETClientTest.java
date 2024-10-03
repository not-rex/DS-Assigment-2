package com.weatherApp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GETClientTest {

    private MockWebServer mockWebServer;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;
    private ByteArrayOutputStream outContent;
    private ByteArrayOutputStream errContent;

    @BeforeAll
    public void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        // Redirect System.out and System.err
        outContent = new ByteArrayOutputStream();
        errContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
    }

    @AfterAll
    public void tearDown() throws IOException {
        mockWebServer.shutdown();

        // Restore System.out and System.err
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @BeforeEach
    public void resetStreams() {
        outContent.reset();
        errContent.reset();
    }

    // Test sending a valid GET request and receiving data.
    @Test
    public void testSendGetRequestSuccess() throws IOException {
        // Prepare mock response
        WeatherEntry entry1 = createWeatherEntry("W100");
        WeatherEntry entry2 = createWeatherEntry("W101");
        List<WeatherEntry> entries = List.of(entry1, entry2);
        String jsonResponse = objectMapper.writeValueAsString(entries);

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Lamport-Time", "5")
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json"));

        // Execute GETClient
        String serverUrl = mockWebServer.url("/").toString();
        String[] args = {serverUrl, "W100"};
        GETClient.main(args);

        // Prepare expected output
        String ls = System.lineSeparator();
        String expectedOutput = "ID: W100" + ls +
                "Name: Test Station A" + ls +
                "State: State A" + ls +
                "Time Zone: TZA" + ls +
                "Latitude: 10.0" + ls +
                "Longitude: 20.0" + ls +
                "Local Date Time: 2024-10-03T12:00:00" + ls +
                "Local Date Time Full: Thursday, October 3, 2024 12:00:00 PM" + ls +
                "Air Temperature: 25.0" + ls +
                "Apparent Temperature: 27.0" + ls +
                "Cloud: Sunny" + ls +
                "Dew Point: 15.0" + ls +
                "Pressure: 1010.0" + ls +
                "Relative Humidity: 50" + ls +
                "Wind Direction: NE" + ls +
                "Wind Speed (KM/H): 10" + ls +
                "Wind Speed (KT): 5" + ls +
                "----------" + ls +
                "ID: W101" + ls +
                "Name: Test Station A" + ls +
                "State: State A" + ls +
                "Time Zone: TZA" + ls +
                "Latitude: 10.0" + ls +
                "Longitude: 20.0" + ls +
                "Local Date Time: 2024-10-03T12:00:00" + ls +
                "Local Date Time Full: Thursday, October 3, 2024 12:00:00 PM" + ls +
                "Air Temperature: 25.0" + ls +
                "Apparent Temperature: 27.0" + ls +
                "Cloud: Sunny" + ls +
                "Dew Point: 15.0" + ls +
                "Pressure: 1010.0" + ls +
                "Relative Humidity: 50" + ls +
                "Wind Direction: NE" + ls +
                "Wind Speed (KM/H): 10" + ls +
                "Wind Speed (KT): 5" + ls +
                "----------" + ls;

        assertEquals(expectedOutput, outContent.toString());
        assertEquals("", errContent.toString());
    }

    // Test sending a GET request and receiving 204 No Content.
    @Test
    public void testSendGetRequestNoContent() throws IOException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(204));

        // Execute GETClient without stationId
        String serverUrl = mockWebServer.url("/").toString();
        String[] args = {serverUrl};
        GETClient.main(args);

        // Prepare expected output with system-specific line separators
        String ls = System.lineSeparator();
        String expectedOutput = "No Content." + ls;

        assertEquals(expectedOutput, outContent.toString());
        assertEquals("", errContent.toString());
    }

    // Test sending a GET request and receiving a non-200/204 response.
    @Test
    public void testSendGetRequestFailure() throws IOException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500));

        // Execute GETClient
        String serverUrl = mockWebServer.url("/").toString();
        String[] args = {serverUrl};
        GETClient.main(args);

        // Prepare expected output with system-specific line separators
        String ls = System.lineSeparator();
        String expectedOutput = "GET request failed with code: 500" + ls;

        assertEquals(expectedOutput, outContent.toString());
        assertEquals("", errContent.toString());
    }

    // Test handling invalid JSON response.
    @Test
    public void testSendGetRequestInvalidJson() throws IOException {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Lamport-Time", "5")
                .setBody("Invalid JSON")
                .addHeader("Content-Type", "application/json"));

        // Execute GETClient
        String serverUrl = mockWebServer.url("/").toString();
        String[] args = {serverUrl};
        GETClient.main(args);

        // Prepare expected error output with system-specific line separators
        String ls = System.lineSeparator();
        String expectedErrorStart = "Failed to parse JSON response: Unrecognized token 'Invalid': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')" + ls;
        String expectedErrorEnd = " at [Source: (String)\"Invalid JSON\"; line: 1, column: 8]" + ls;

        String actualError = errContent.toString();

        // Due to slight differences in column number, check if it starts with the expected message
        assertTrue(actualError.startsWith("Failed to parse JSON response: Unrecognized token 'Invalid': was expecting (JSON String, Number, Array, Object or token 'null', 'true' or 'false')"));
        assertTrue(actualError.contains("at [Source: (String)\"Invalid JSON\"; line: 1, column: "));
        assertTrue(actualError.contains("]"));
        assertEquals("", outContent.toString());
    }

    // Test handling invalid Lamport-Time header.
    @Test
    public void testSendGetRequestInvalidLamportTimeHeader() throws IOException {
        WeatherEntry entry = createWeatherEntry("W100");
        List<WeatherEntry> entries = List.of(entry);
        String jsonResponse = objectMapper.writeValueAsString(entries);

        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(200)
                .addHeader("Lamport-Time", "invalid")
                .setBody(jsonResponse)
                .addHeader("Content-Type", "application/json"));

        // Execute GETClient
        String serverUrl = mockWebServer.url("/").toString();
        String[] args = {serverUrl};
        GETClient.main(args);

        // Prepare expected output and error with system-specific line separators
        String ls = System.lineSeparator();
        String expectedOutput = "ID: W100" + ls +
                "Name: Test Station A" + ls +
                "State: State A" + ls +
                "Time Zone: TZA" + ls +
                "Latitude: 10.0" + ls +
                "Longitude: 20.0" + ls +
                "Local Date Time: 2024-10-03T12:00:00" + ls +
                "Local Date Time Full: Thursday, October 3, 2024 12:00:00 PM" + ls +
                "Air Temperature: 25.0" + ls +
                "Apparent Temperature: 27.0" + ls +
                "Cloud: Sunny" + ls +
                "Dew Point: 15.0" + ls +
                "Pressure: 1010.0" + ls +
                "Relative Humidity: 50" + ls +
                "Wind Direction: NE" + ls +
                "Wind Speed (KM/H): 10" + ls +
                "Wind Speed (KT): 5" + ls +
                "----------" + ls;

        String expectedError = "Invalid Lamport-Time header in response." + ls;

        assertEquals(expectedOutput, outContent.toString());
        assertEquals(expectedError, errContent.toString());
    }

    // Helper function to create a WeatherEntry object.
    private WeatherEntry createWeatherEntry(String id) {
        WeatherEntry entry = new WeatherEntry();
        entry.setId(id);
        entry.setName("Test Station A");
        entry.setState("State A");
        entry.setTimeZone("TZA");
        entry.setLat(10.0);
        entry.setLon(20.0);
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
}
