.DEFAULT_GOAL := help

.PHONY: help run build test clean package

help:
	@echo "Usage: make <target>"
	@echo ""
	@echo "  run      Start the application (http://localhost:6419)"
	@echo "  build    Compile without running tests"
	@echo "  test     Run unit tests"
	@echo "  package  Build executable JAR"
	@echo "  clean    Remove build artifacts"

run:
	./mvnw spring-boot:run

build:
	./mvnw compile -q

test:
	./mvnw test

package:
	./mvnw package -DskipTests -q

clean:
	./mvnw clean -q
