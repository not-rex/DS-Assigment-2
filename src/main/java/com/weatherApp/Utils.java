package com.weatherApp;

public class Utils {

    /**
     * Parses and formats the server URL.
     *
     * @param input The input URL string.
     * @return A properly formatted URL string.
     */
    public static String parseURL(String input) {
        if (!input.startsWith("http://") && !input.startsWith("https://")) {
            input = "http://" + input;
        }
        return input;
    }

    /**
     * Retries a given operation a specified number of times with delays.
     *
     * @param operation The operation to retry.
     * @param retries   Number of retry attempts.
     * @param delay     Delay between retries in milliseconds.
     */
    public static void retryOperation(Runnable operation, int retries, long delay) {
        while (retries > 0) {
            try {
                operation.run();
                return; // Success
            } catch (Exception e) {
                retries--;
                if (retries == 0) {
                    throw e;
                }
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Operation interrupted during retry.", ie);
                }
            }
        }
    }
}
