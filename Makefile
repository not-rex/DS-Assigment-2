JAVAC = javac
JAR = jar
SRC_DIR = src/main/java
BIN_DIR = bin
PACKAGE_DIR = com/weatherApp
JAR_FILE = weatherApp.jar

SOURCES := $(shell find $(SRC_DIR)/$(PACKAGE_DIR) -name "*.java")

.PHONY: all clean debug

all: debug $(JAR_FILE)

debug:
	@echo "Checking Java version..."
	@java -version || echo "Java not found"
	@echo "Checking javac version..."
	@$(JAVAC) -version || echo "javac not found"
	@echo "Current PATH: $(PATH)"
	@echo "JAVA_HOME: $(JAVA_HOME)"
	@echo "Source files found: $(SOURCES)"

$(JAR_FILE): $(SOURCES)
	@mkdir -p $(BIN_DIR)
	$(JAVAC) -d $(BIN_DIR) $(SOURCES)
	$(JAR) cf $(JAR_FILE) -C $(BIN_DIR) .

clean:
	rm -rf $(BIN_DIR) $(JAR_FILE)

run: $(JAR_FILE)
	java -cp $(JAR_FILE) $(PACKAGE_DIR).AggregationServer
