package com.weatherApp;

import java.io.IOException;

public interface HttpService {
    /**
     * Sends an HTTP PUT request to the specified URL with the given JSON data and Lamport time.
     *
     * @param url         The endpoint URL.
     * @param jsonData    The JSON payload as a String.
     * @param lamportTime The Lamport timestamp.
     * @return The HTTP response code.
     * @throws IOException If an I/O error occurs.
     */
    int sendPut(String url, String jsonData, int lamportTime) throws IOException;
}
