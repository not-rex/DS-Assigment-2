package com.weatherApp;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.util.Properties;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;

public class ContentServer {
    private static LamportClock clock = new LamportClock();
    private static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) {
        Properties prop = new Properties();
        try (InputStream input = ContentServer.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            prop.load(input);
            String serverUrl = prop.getProperty("server.url");
            String filePath = prop.getProperty("data.filePath");

            WeatherEntry jsonData = readFile(filePath);
            sendPutRequest(serverUrl, jsonData);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    private static String parseServerUrl(String input) {
        if (!input.startsWith("http://") && !input.startsWith("https://")) {
            input = "http://" + input;
        }
        return input;
    }

    /**
     * Reads the local weather data file and converts it into a WeatherEntry object.
     *
     * @param filePath The path to the local weather data file.
     * @return A WeatherEntry object populated with data from the file.
     * @throws IOException If an I/O error occurs.
     */
    private static WeatherEntry readFile(String filePath) throws IOException {
        WeatherEntry weatherEntry = new WeatherEntry();
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOException("File not found: " + filePath);
        }

        BufferedReader reader = Files.newBufferedReader(file.toPath());
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(":", 2);
            if (parts.length != 2) continue; // Skip invalid lines
            String key = parts[0].trim();
            String value = parts[1].trim();

            switch (key) {
                case "id":
                    weatherEntry.setId(value);
                    break;
                case "name":
                    weatherEntry.setName(value);
                    break;
                case "state":
                    weatherEntry.setState(value);
                    break;
                case "time_zone":
                    weatherEntry.setTimeZone(value);
                    break;
                case "lat":
                    weatherEntry.setLat(Double.parseDouble(value));
                    break;
                case "lon":
                    weatherEntry.setLon(Double.parseDouble(value));
                    break;
                case "local_date_time":
                    weatherEntry.setLocalDateTime(value);
                    break;
                case "local_date_time_full":
                    weatherEntry.setLocalDateTimeFull(value);
                    break;
                case "air_temp":
                    weatherEntry.setAirTemp(Double.parseDouble(value));
                    break;
                case "apparent_t":
                    weatherEntry.setApparentT(Double.parseDouble(value));
                    break;
                case "cloud":
                    weatherEntry.setCloud(value);
                    break;
                case "dewpt":
                    weatherEntry.setDewpt(Double.parseDouble(value));
                    break;
                case "press":
                    weatherEntry.setPress(Double.parseDouble(value));
                    break;
                case "rel_hum":
                    weatherEntry.setRelHum(Integer.parseInt(value));
                    break;
                case "wind_dir":
                    weatherEntry.setWindDir(value);
                    break;
                case "wind_spd_kmh":
                    weatherEntry.setWindSpdKmh(Integer.parseInt(value));
                    break;
                case "wind_spd_kt":
                    weatherEntry.setWindSpdKt(Integer.parseInt(value));
                    break;
                default:
                    // Ignore unknown keys
                    break;
            }
        }
        reader.close();

        if (weatherEntry.getId() == null || weatherEntry.getId().isEmpty()) {
            throw new IOException("Missing 'id' in weather data.");
        }

        return weatherEntry;
    }

    /**
     * Sends an HTTP PUT request to the server with the weather data.
     *
     * @param serverUrl  The base URL of the server.
     * @param jsonData   The WeatherEntry object to send.
     * @throws IOException If an I/O error occurs.
     */
    private static void sendPutRequest(String serverUrl, WeatherEntry jsonData) throws IOException {
        clock.tick();
        String endpoint = serverUrl + "/weather.json";
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Lamport-Time", String.valueOf(clock.getTime()));
        conn.setDoOutput(true);

        // Serialize WeatherEntry to JSON
        String jsonString = objectMapper.writeValueAsString(jsonData);

        OutputStream os = conn.getOutputStream();
        os.write(jsonString.getBytes());
        os.flush();
        os.close();

        int responseCode = conn.getResponseCode();
        if (responseCode == 200 || responseCode == 201) {
            String responseTime = conn.getHeaderField("Lamport-Time");
            if (responseTime != null) {
                try {
                    int receivedTime = Integer.parseInt(responseTime);
                    clock.update(receivedTime);
                } catch (NumberFormatException e) {
                    System.err.println("Invalid Lamport-Time header in response.");
                }
            }
            System.out.println("Data uploaded successfully with response code: " + responseCode);
        } else {
            System.out.println("PUT request failed with code: " + responseCode);
            // Optionally implement retry logic
            retryPutRequest(serverUrl, jsonData, 3);
        }
    }

    /**
     * Implements retry logic for PUT requests with exponential backoff.
     *
     * @param serverUrl  The base URL of the server.
     * @param jsonData   The WeatherEntry object to send.
     * @param retries    Number of retry attempts.
     */
    private static void retryPutRequest(String serverUrl, WeatherEntry jsonData, int retries) {
        while (retries > 0) {
            try {
                Thread.sleep(1000); // Wait before retrying
                sendPutRequest(serverUrl, jsonData);
                return; // Success
            } catch (IOException | InterruptedException e) {
                retries--;
                System.err.println("Retrying PUT request... Attempts left: " + retries);
                if (retries == 0) {
                    System.err.println("Failed to upload data after multiple attempts.");
                }
            }
        }
    }
}
