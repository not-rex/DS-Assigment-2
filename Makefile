JAR_FILE = target/WeatherAggregationSystem-1.0-SNAPSHOT.jar
PACKAGE_DIR = com.weatherApp

.PHONY: run clean

run: $(JAR_FILE)
	java -cp $(JAR_FILE) $(PACKAGE_DIR).AggregationServer

clean:
	rm -rf $(JAR_FILE)
