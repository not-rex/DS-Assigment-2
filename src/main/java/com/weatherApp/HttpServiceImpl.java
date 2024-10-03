package com.weatherApp;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpServiceImpl implements HttpService {
    private static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public int sendPut(String serverUrl, String jsonData, int lamportTime) throws IOException {
        String endpoint = serverUrl + "/weather.json";
        URL url = new URL(endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("PUT");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Lamport-Time", String.valueOf(lamportTime));
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        os.write(jsonData.getBytes());
        os.flush();
        os.close();

        return conn.getResponseCode();
    }
}
