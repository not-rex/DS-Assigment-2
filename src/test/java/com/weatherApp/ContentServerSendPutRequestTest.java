package com.weatherApp;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ContentServerSendPutRequestTest {
    private HttpService mockHttpService;

    @BeforeEach
    public void setUp() {
        mockHttpService = mock(HttpService.class);
        ContentServer.setHttpService(mockHttpService);
    }

    @Test
    public void testSendPutRequestSuccess() throws IOException {
        // Arrange
        String serverUrl = "http://localhost:4567";
        WeatherEntry entry = new WeatherEntry();
        entry.setId("W001");

        // Use serverUrl without appending "/weather.json"
        when(mockHttpService.sendPut(eq(serverUrl), anyString(), anyInt())).thenReturn(201);

        ContentServer.sendPutRequest(serverUrl, entry);
        verify(mockHttpService, times(1)).sendPut(eq(serverUrl), anyString(), anyInt());
    }

    @Test
    public void testSendPutRequestFailureWithRetry() throws IOException {
        // Arrange
        String serverUrl = "http://localhost:4567";
        WeatherEntry entry = new WeatherEntry();
        entry.setId("W001");

        // First attempt throws IOException, second attempt returns 200
        when(mockHttpService.sendPut(eq(serverUrl), anyString(), anyInt()))
            .thenThrow(new IOException("Simulated Connection Error"))
            .thenReturn(200);

        ContentServer.sendPutRequest(serverUrl, entry);
        // sendPut is called twice: initial attempt + one retry
        verify(mockHttpService, times(2)).sendPut(eq(serverUrl), anyString(), anyInt());
    }

    @Test
    public void testSendPutRequestAllRetriesFail() throws IOException {
        // Arrange
        String serverUrl = "http://localhost:4567";
        WeatherEntry entry = new WeatherEntry();
        entry.setId("W001");

        // All attempts throw IOException
        when(mockHttpService.sendPut(eq(serverUrl), anyString(), anyInt()))
            .thenThrow(new IOException("Simulated Connection Error"))
            .thenThrow(new IOException("Simulated Connection Error"))
            .thenThrow(new IOException("Simulated Connection Error"))
            .thenThrow(new IOException("Simulated Connection Error")); // Extra throw to ensure retries

        ContentServer.sendPutRequest(serverUrl, entry);
        // sendPut is called four times: initial attempt + three retries
        verify(mockHttpService, times(4)).sendPut(eq(serverUrl), anyString(), anyInt());
    }
}
