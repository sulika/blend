.DEFAULT_GOAL := help

.PHONY: help run build test clean package setup docker-build docker-up docker-down

help:
	@echo "Usage: make <target>"
	@echo ""
	@echo "  setup         Interactive wizard: configure sources & start via Docker"
	@echo "  run           Start the application locally (http://localhost:8080)"
	@echo "  build         Compile without running tests"
	@echo "  test          Run unit tests"
	@echo "  package       Build executable JAR"
	@echo "  clean         Remove build artifacts"
	@echo "  docker-build  Build the Docker image"
	@echo "  docker-up     Run the app in Docker (via docker-compose)"
	@echo "  docker-down   Stop the Docker container"

setup:
	./setup.sh

run:
	SPRING_CONFIG_ADDITIONAL_LOCATION=optional:file:./application.yml ./mvnw spring-boot:run

build:
	./mvnw compile -q

test:
	./mvnw test

package:
	./mvnw package -DskipTests -q

clean:
	./mvnw clean -q

docker-build:
	docker compose build

docker-up:
	docker compose up

docker-down:
	docker compose down
