JDK_ARCHIVE = jdk-17_linux-x64_bin.tar.gz
JDK_DIR = jdk-17.0.12
JDK_URL = https://download.oracle.com/java/17/latest/jdk-17_linux-x64_bin.tar.gz
JAVA = $(JDK_DIR)/bin/java
JAR_FILE = target/WeatherAggregationSystem-1.0-SNAPSHOT.jar
PACKAGE_DIR = com.weatherApp

.PHONY: run clean debug

run: check-jdk $(JAR_FILE)
	@echo "Running the application with Portable JDK..."
	@$(JAVA) -cp $(JAR_FILE) $(PACKAGE_DIR).AggregationServer

check-jdk:
	@if [ ! -d $(JDK_DIR) ]; then \
		if [ ! -f $(JDK_ARCHIVE) ]; then \
			echo "Downloading JDK..."; \
			curl -o $(JDK_ARCHIVE) $(JDK_URL); \
		fi; \
		echo "Extracting JDK..."; \
		tar -xzf $(JDK_ARCHIVE); \
	fi

debug:
	@echo "Checking if Portable Java is available..."
	@$(JAVA) -version || echo "Portable Java not found"
	@echo "Checking if JAR file exists..."
	@[ -f $(JAR_FILE) ] && echo "JAR file exists: $(JAR_FILE)" || echo "JAR file not found: $(JAR_FILE)"

clean:
	rm -rf $(JDK_DIR) $(JDK_ARCHIVE) $(JAR_FILE)
