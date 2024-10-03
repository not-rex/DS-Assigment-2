package com.weatherApp;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import spark.Spark;

import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AggregationServerIntegrationTest {

    private static Thread serverThread;
    private static Path tempDataStorePath;
    private static int serverPort = 4567;

    // Starts the AggregationServer.
    @BeforeAll
    public void setUp() throws IOException {
        // Create a temporary file for dataStore to test isolation
        tempDataStorePath = Files.createTempFile("weather_data_test", ".json");
        // Delete the file to start fresh
        Files.deleteIfExists(tempDataStorePath);
        
        // Start AggregationServer in a separate thread
        serverThread = new Thread(() -> {
            AggregationServer.main(new String[]{String.valueOf(serverPort), tempDataStorePath.toString()});
        });
        serverThread.start();

        // Configure RestAssured to point to the server
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = serverPort;

        // Wait for the server to start
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Stops the AggregationServer and cleans up after all tests.
    @AfterAll
    public void tearDown() {
        // Stop the server
        Spark.stop();
        // Interrupt the server thread
        serverThread.interrupt();
        // Delete the temporary dataStore file
        try {
            Files.deleteIfExists(tempDataStorePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Test case for sending a valid PUT request.
    @Test
    public void testPutWeatherDataSuccess() {
        WeatherEntry entry = new WeatherEntry();
        entry.setId("W200");
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

        given()
            .contentType(ContentType.JSON)
            .header("Lamport-Time", "1")
            .body(entry)
        .when()
            .put("/weather.json")
        .then()
            .statusCode(anyOf(is(200), is(201)))
            .body(equalTo("Data Received"));
    }

    // Test case for sending a PUT request without the Lamport-Time header.
    @Test
    public void testPutWeatherDataMissingLamportTime() {
        WeatherEntry entry = new WeatherEntry();
        entry.setId("W201");
        entry.setName("Test Station B");
        entry.setState("State B");
        entry.setTimeZone("TZB");
        entry.setLat(30.0);
        entry.setLon(40.0);
        entry.setLocalDateTime("2024-10-04T13:00:00");
        entry.setLocalDateTimeFull("Friday, October 4, 2024 1:00:00 PM");
        entry.setAirTemp(30.0);
        entry.setApparentT(32.0);
        entry.setCloud("Cloudy");
        entry.setDewpt(18.0);
        entry.setPress(1008.0);
        entry.setRelHum(60);
        entry.setWindDir("SW");
        entry.setWindSpdKmh(15);
        entry.setWindSpdKt(8);
        entry.setTimestamp(2);
        entry.setLastUpdated(System.currentTimeMillis());

        given()
            .contentType(ContentType.JSON)
            .body(entry)
        .when()
            .put("/weather.json")
        .then()
            .statusCode(400)
            .body(equalTo("Missing Lamport-Time header"));
    }

    // Test case for sending a PUT request with an invalid Lamport-Time header.
    @Test
    public void testPutWeatherDataInvalidLamportTime() {
        WeatherEntry entry = new WeatherEntry();
        entry.setId("W202");
        entry.setName("Test Station C");
        entry.setState("State C");
        entry.setTimeZone("TZC");
        entry.setLat(50.0);
        entry.setLon(60.0);
        entry.setLocalDateTime("2024-10-05T14:00:00");
        entry.setLocalDateTimeFull("Saturday, October 5, 2024 2:00:00 PM");
        entry.setAirTemp(20.0);
        entry.setApparentT(22.0);
        entry.setCloud("Rainy");
        entry.setDewpt(10.0);
        entry.setPress(1012.0);
        entry.setRelHum(55);
        entry.setWindDir("NW");
        entry.setWindSpdKmh(12);
        entry.setWindSpdKt(6);
        entry.setTimestamp(3);
        entry.setLastUpdated(System.currentTimeMillis());

        given()
            .contentType(ContentType.JSON)
            .header("Lamport-Time", "invalid")
            .body(entry)
        .when()
            .put("/weather.json")
        .then()
            .statusCode(400)
            .body(equalTo("Invalid Lamport-Time header"));
    }

    // Test case for retrieving weather data with a valid Lamport-Time header.
    @Test
    public void testGetWeatherDataSuccess() {
        // First, send a PUT request to add data
        WeatherEntry entry = new WeatherEntry();
        entry.setId("W203");
        entry.setName("Test Station D");
        entry.setState("State D");
        entry.setTimeZone("TZD");
        entry.setLat(70.0);
        entry.setLon(80.0);
        entry.setLocalDateTime("2024-10-06T15:00:00");
        entry.setLocalDateTimeFull("Sunday, October 6, 2024 3:00:00 PM");
        entry.setAirTemp(28.0);
        entry.setApparentT(30.0);
        entry.setCloud("Overcast");
        entry.setDewpt(20.0);
        entry.setPress(1005.0);
        entry.setRelHum(70);
        entry.setWindDir("SE");
        entry.setWindSpdKmh(18);
        entry.setWindSpdKt(10);
        entry.setTimestamp(4);
        entry.setLastUpdated(System.currentTimeMillis());

        given()
            .contentType(ContentType.JSON)
            .header("Lamport-Time", "2")
            .body(entry)
        .when()
            .put("/weather.json")
        .then()
            .statusCode(anyOf(is(200), is(201)))
            .body(equalTo("Data Received"));

        // Retrieve the data
        given()
            .header("Lamport-Time", "3")
        .when()
            .get("/weather.json")
        .then()
            .statusCode(200)
            .contentType(ContentType.JSON)
            .body("id", hasItems("W200", "W203"));
    }

    // Test case for retrieving weather data without the Lamport-Time header.
    @Test
    public void testGetWeatherDataMissingLamportTime() {
        given()
            // Missing Lamport-Time header
        .when()
            .get("/weather.json")
        .then()
            .statusCode(400)
            .body(equalTo("Missing Lamport-Time header"));
    }

    // Test case for retrieving weather data with an invalid Lamport-Time header.
    @Test
    public void testGetWeatherDataInvalidLamportTime() {
        given()
            .header("Lamport-Time", "invalid") // Invalid Lamport-Time
        .when()
            .get("/weather.json")
        .then()
            .statusCode(400)
            .body(equalTo("Invalid Lamport-Time header"));
    }

    // Test case for sending a PUT request with an empty body.
    @Test
    public void testPutWeatherDataNoContent() {
        given()
            .contentType(ContentType.JSON)
            .header("Lamport-Time", "4")
            .body("") // Empty body
        .when()
            .put("/weather.json")
        .then()
            .statusCode(204)
            .body(is(emptyOrNullString()));
    }

    // Test case for sending a PUT request with an invalid Content-Type.
    @Test
    public void testPutWeatherDataInvalidContentType() throws IOException {
        WeatherEntry entry = new WeatherEntry();
        entry.setId("W204");
        entry.setName("Test Station E");
        entry.setState("State E");
        entry.setTimeZone("TZE");
        entry.setLat(90.0);
        entry.setLon(100.0);
        entry.setLocalDateTime("2024-10-07T16:00:00");
        entry.setLocalDateTimeFull("Monday, October 7, 2024 4:00:00 PM");
        entry.setAirTemp(22.0);
        entry.setApparentT(24.0);
        entry.setCloud("Foggy");
        entry.setDewpt(25.0);
        entry.setPress(1003.0);
        entry.setRelHum(80);
        entry.setWindDir("NE");
        entry.setWindSpdKmh(20);
        entry.setWindSpdKt(11);
        entry.setTimestamp(5);
        entry.setLastUpdated(System.currentTimeMillis());

        // Serialize WeatherEntry to JSON
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonBody = objectMapper.writeValueAsString(entry);

        given()
            .contentType(ContentType.TEXT) // Invalid
            .header("Lamport-Time", "5")
            .body(jsonBody)
        .when()
            .put("/weather.json")
        .then()
            .statusCode(400)
            .body(equalTo("Invalid Content-Type"));
    }
}
