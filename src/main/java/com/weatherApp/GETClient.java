package com.weatherApp;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.io.IOException;

public class GETClient {
    private static LamportClock clock = new LamportClock();
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java GETClient <server_url> [station_id]");
            return;
        }

        String serverUrl = parseServerUrl(args[0]);
        String stationId = args.length > 1 ? args[1] : null;

        try {
            sendGetRequest(serverUrl, stationId);
        } catch (IOException e) {
            System.err.println("GET request failed: " + e.getMessage());
        }
    }

    private static String parseServerUrl(String input) {
        if (!input.startsWith("http://") && !input.startsWith("https://")) {
            input = "http://" + input;
        }
        return input;
    }

    /**
     * Sends an HTTP GET request to the server and displays the weather data.
     *
     * @param serverUrl The base URL of the server.
     * @param stationId Optional station ID to filter the data.
     * @throws IOException If an I/O error occurs.
     */
    private static void sendGetRequest(String serverUrl, String stationId) throws IOException {
        clock.tick();
        String endpoint = serverUrl + "/weather.json" + (stationId != null ? "?id=" + stationId : "");
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Lamport-Time", String.valueOf(clock.getTime()));

        int responseCode = conn.getResponseCode();
        if (responseCode == 200) {
            String responseLamportTime = conn.getHeaderField("Lamport-Time");
            if (responseLamportTime != null) {
                try {
                    int receivedTime = Integer.parseInt(responseLamportTime);
                    clock.update(receivedTime);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid Lamport-Time header in response.");
                }
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder jsonResponseBuilder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                jsonResponseBuilder.append(line);
            }
            in.close();

            String jsonResponse = jsonResponseBuilder.toString();
            displayData(jsonResponse);
        } else if (responseCode == 204) {
            System.out.println("No Content.");
        } else {
            System.out.println("GET request failed with code: " + responseCode);
        }
    }

    /**
     * Parses and displays the weather data in a readable format.
     *
     * @param jsonResponse The JSON string received from the server.
     */
    private static void displayData(String jsonResponse) {
        try {
            List<WeatherEntry> entries = objectMapper.readValue(jsonResponse, new TypeReference<List<WeatherEntry>>() {});
            for (WeatherEntry entry : entries) {
                System.out.println("ID: " + entry.getId());
                System.out.println("Name: " + entry.getName());
                System.out.println("State: " + entry.getState());
                System.out.println("Time Zone: " + entry.getTimeZone());
                System.out.println("Latitude: " + entry.getLat());
                System.out.println("Longitude: " + entry.getLon());
                System.out.println("Local Date Time: " + entry.getLocalDateTime());
                System.out.println("Local Date Time Full: " + entry.getLocalDateTimeFull());
                System.out.println("Air Temperature: " + entry.getAirTemp());
                System.out.println("Apparent Temperature: " + entry.getApparentT());
                System.out.println("Cloud: " + entry.getCloud());
                System.out.println("Dew Point: " + entry.getDewpt());
                System.out.println("Pressure: " + entry.getPress());
                System.out.println("Relative Humidity: " + entry.getRelHum());
                System.out.println("Wind Direction: " + entry.getWindDir());
                System.out.println("Wind Speed (KM/H): " + entry.getWindSpdKmh());
                System.out.println("Wind Speed (KT): " + entry.getWindSpdKt());
                System.out.println("----------");
            }
        } catch (IOException e) {
            System.err.println("Failed to parse JSON response: " + e.getMessage());
        }
    }
}
