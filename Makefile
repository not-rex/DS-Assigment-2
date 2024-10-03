JAR_FILE = target/WeatherAggregationSystem-1.0-SNAPSHOT.jar
PACKAGE_DIR = com.weatherApp
JAVA_CMD = java
JDK_DIR = jdk-17
PORTABLE_JAVA = $(JDK_DIR)/bin/java

.PHONY: run clean os-check debug

os-check:
	@echo "Checking the operating system..."
	@if uname -a | grep -i "linux"; then \
		echo "Linux system detected"; \
	elif uname -a | grep -i "darwin"; then \
		echo "macOS system detected"; \
	else \
		echo "Unknown operating system, defaulting to generic run"; \
	fi

debug:
	@echo "Checking Java version..."
	@$(JAVA_CMD) -version || echo "Java not found or not in PATH"
	@echo "Checking if JAR file exists..."
	@[ -f $(JAR_FILE) ] && echo "JAR file exists: $(JAR_FILE)" || echo "JAR file not found: $(JAR_FILE)"
	@echo "Current PATH: $(PATH)"
	@echo "Checking if required class files are in the JAR..."
	@$(JAVA_CMD) -cp $(JAR_FILE) $(PACKAGE_DIR).AggregationServer || echo "AggregationServer not found or failed to execute"

run: os-check $(JAR_FILE)
	@echo "Attempting to run with system Java..."
	@$(JAVA_CMD) -cp $(JAR_FILE) $(PACKAGE_DIR).AggregationServer

run-portable: $(JAR_FILE)
	@echo "Running the application with Portable JDK..."
	@$(PORTABLE_JAVA) -cp $(JAR_FILE) $(PACKAGE_DIR).AggregationServer

clean:
	rm -rf $(JAR_FILE)
