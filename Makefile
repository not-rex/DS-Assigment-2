JAVAC = javac
JAR = jar
SRC_DIR = src/main/java
BIN_DIR = bin
PACKAGE_DIR = com/weatherApp
JAR_FILE = weatherApp.jar

SOURCES := $(shell find $(SRC_DIR)/$(PACKAGE_DIR) -name "*.java")

all: $(JAR_FILE)

$(JAR_FILE): $(SOURCES)
	@mkdir -p $(BIN_DIR)
	$(JAVAC) -d $(BIN_DIR) $(SOURCES)
	$(JAR) cf $(JAR_FILE) -C $(BIN_DIR) .

clean:
	rm -rf $(BIN_DIR) $(JAR_FILE)

run: $(JAR_FILE)
	java -cp $(JAR_FILE) $(PACKAGE_DIR).AggregationServer
