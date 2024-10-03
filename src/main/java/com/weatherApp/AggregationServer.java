package com.weatherApp;

import com.fasterxml.jackson.databind.ObjectMapper;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import java.io.IOException;
import java.util.List;

public class AggregationServer {
    private static int port = 4567;
    private static LamportClock clock = new LamportClock();
    private static WeatherDataStore dataStore;
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        String dataStorePath = "data/weather_data.json"; // Default path
        if (args.length > 0) {
            port = Integer.parseInt(args[0]);
        }
        if (args.length > 1) {
            dataStorePath = args[1];
        }
        dataStore = new WeatherDataStore(dataStorePath);
        startServer();
    }

    private static void startServer() {
        // Initialize server (using Spark Java)
        Spark.port(port);

        // Load persisted data
        dataStore.loadPersistedData();

        // Schedule data expiration
        scheduleDataExpiration();

        // Define routes
        Spark.put("/weather.json", (request, response) -> handlePut(request, response));
        Spark.get("/weather.json", (request, response) -> handleGet(request, response));

        // Handle invalid routes
        Spark.notFound((req, res) -> {
            res.status(400);
            return "Bad Request";
        });
    }

    /**
     * Handles PUT requests to update weather data.
     *
     * @param request  The incoming HTTP request.
     * @param response The HTTP response to be sent.
     * @return A message indicating the result of the operation.
     */
    private static String handlePut(Request request, Response response) {
        String lamportTimeHeader = request.headers("Lamport-Time");
        if (lamportTimeHeader == null) {
            response.status(400);
            return "Missing Lamport-Time header";
        }

        int receivedTime;
        try {
            receivedTime = Integer.parseInt(lamportTimeHeader);
        } catch (NumberFormatException e) {
            response.status(400);
            return "Invalid Lamport-Time header";
        }

        clock.update(receivedTime);

        String contentType = request.contentType();
        if (contentType == null || !contentType.contains("application/json")) {
            response.status(400);
            return "Invalid Content-Type";
        }

        String body = request.body();
        if (body == null || body.isEmpty()) {
            response.status(204);
            return "No Content";
        }

        // Parse JSON and validate using Jackson
        WeatherEntry weatherEntry;
        try {
            weatherEntry = objectMapper.readValue(body, WeatherEntry.class);
            if (weatherEntry.getId() == null || weatherEntry.getId().isEmpty()) {
                response.status(500);
                return "Invalid JSON: Missing 'id'";
            }
        } catch (IOException e) {
            response.status(500);
            return "Invalid JSON format";
        }

        // Update timestamp and last updated time
        weatherEntry.setTimestamp(clock.getTime());
        weatherEntry.setLastUpdated(System.currentTimeMillis());

        // Store data
        dataStore.saveData(weatherEntry, clock.getTime());

        // Persist data
        try {
            dataStore.persistData();
        } catch (IOException e) {
            response.status(500);
            return "Failed to persist data";
        }

        // Determine response status
        if (dataStore.isNewEntry(weatherEntry.getId())) {
            response.status(201); // Created
        } else {
            response.status(200); // OK
        }

        return "Data Received";
    }

    /**
     * Handles GET requests to retrieve aggregated weather data.
     *
     * @param request  The incoming HTTP request.
     * @param response The HTTP response to be sent.
     * @return JSON string of aggregated weather data.
     */
    private static String handleGet(Request request, Response response) {
        String lamportTimeHeader = request.headers("Lamport-Time");
        if (lamportTimeHeader == null) {
            response.status(400);
            return "Missing Lamport-Time header";
        }

        int receivedTime;
        try {
            receivedTime = Integer.parseInt(lamportTimeHeader);
        } catch (NumberFormatException e) {
            response.status(400);
            return "Invalid Lamport-Time header";
        }

        clock.update(receivedTime);

        List<WeatherEntry> data = dataStore.getData();

        response.type("application/json");
        response.status(200);
        try {
            // Serialize the list to JSON
            return objectMapper.writeValueAsString(data);
        } catch (IOException e) {
            response.status(500);
            return "Failed to serialize data";
        }
    }

    /**
     * Schedules periodic data expiration tasks.
     */
    private static void scheduleDataExpiration() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            dataStore.expireOldData();
            try {
                dataStore.persistData();
            } catch (IOException e) {
                System.err.println("Error persisting data: " + e.getMessage());
            }
        }, 0, 5, TimeUnit.SECONDS);
    }
}
