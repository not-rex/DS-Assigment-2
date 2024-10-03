package com.weatherApp;

import java.io.IOException;

public interface HttpService {
    // Sends HTTP PUT request to the specified URL with the JSON data and Lamport time.
    int sendPut(String url, String jsonData, int lamportTime) throws IOException;
}
