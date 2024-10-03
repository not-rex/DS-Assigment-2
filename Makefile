MVN = mvn
JAR_FILE = target/weatherApp.jar
SRC_DIR = src/main/java
PACKAGE = com.weatherApp

.PHONY: all clean run

all: $(JAR_FILE)

$(JAR_FILE):
	$(MVN) clean package

run: $(JAR_FILE)
	java -cp $(JAR_FILE) $(PACKAGE).AggregationServer

clean:
	$(MVN) clean
