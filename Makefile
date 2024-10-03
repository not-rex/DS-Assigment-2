JAR_FILE = target/WeatherAggregationSystem-1.0-SNAPSHOT.jar
PACKAGE_DIR = com.weatherApp
JAVA_CMD = java

.PHONY: all run clean debug

all: run

debug:
	@echo "Checking Java version..."
	@$(JAVA_CMD) -version || echo "Java not found or not in PATH"
	@echo "Checking if JAR file exists..."
	@[ -f $(JAR_FILE) ] && echo "JAR file exists: $(JAR_FILE)" || echo "JAR file not found: $(JAR_FILE)"
	@echo "Current PATH: $(PATH)"
	@echo "JAVA_HOME: $(JAVA_HOME)"
	@echo "Checking if required class files are in the JAR..."
	@$(JAVA_CMD) -cp $(JAR_FILE) $(PACKAGE_DIR).AggregationServer || echo "AggregationServer not found or failed to execute"

run: $(JAR_FILE)
	@echo "Running the application..."
	@$(MAKE) debug
	@$(JAVA_CMD) -cp $(JAR_FILE) $(PACKAGE_DIR).AggregationServer

clean:
	rm -rf $(JAR_FILE)
