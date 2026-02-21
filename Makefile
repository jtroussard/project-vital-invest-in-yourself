.PHONY: clean install reinstall test build run help install-fast deps

# Variables
MVNW = ./mvnw

## clean: Clean the project
clean:
	$(MVNW) clean

## install: Install dependencies
install:
	$(MVNW) install

## install-fast: Build the project skipping tests
install-fast:
	$(MVNW) install -DskipTests

## deps: Resolve and download dependencies only
deps:
	$(MVNW) dependency:resolve

## reinstall: Clean and install dependencies
reinstall:
	$(MVNW) clean install

## test: Run tests
test:
	$(MVNW) test

## build: Build the project (package)
build:
	$(MVNW) package

## run: Run the Spring Boot application
run:
	$(MVNW) spring-boot:run

## help: Show this help message
help:
	@echo "Usage: make [target]"
	@echo ""
	@echo "Targets:"
	@grep -E '^##' Makefile | sed -e 's/## //' | column -t -s ':'
