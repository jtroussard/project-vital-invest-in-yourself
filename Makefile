.PHONY: clean install reinstall test build run help install-fast deps

# Variables
MVNW = ./mvnw
-include .env
export $(shell [ -f .env ] && sed 's/=.*//' .env)

## clean: Clean the project
clean:
	$(MVNW) clean

## install: Install dependencies and build JAR
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

## coverage: Run tests and produce a test coverage report
coverage:
	$(MVNW) test jacoco:report

## build: Build the project (package JAR)
build:
	$(MVNW) package

## run: Run the Spring Boot application (defaults to active profile)
run:
	$(MVNW) spring-boot:run

## run-local: Run the app with the 'local' profile (native)
run-local:
	$(MVNW) spring-boot:run -Dspring-boot.run.profiles=local

## run-dev: Run the app with the 'dev' profile (native)
run-dev:
	$(MVNW) spring-boot:run -Dspring-boot.run.profiles=dev

## run-prod: Run the app with the 'prod' profile (native)
run-prod:
	$(MVNW) spring-boot:run -Dspring-boot.run.profiles=prod

# GCP Variables
ENV ?= dev
GCP_PROJECT_ID = project-vital-2026-v1
GCP_REGION = us-central1
GCP_REPO = project-vital-repo-$(ENV)
GCP_APP_NAME = projectvital-$(ENV)
GCP_IMAGE = $(GCP_REGION)-docker.pkg.dev/$(GCP_PROJECT_ID)/$(GCP_REPO)/$(GCP_APP_NAME)

## docker-build: Build the Docker image locally
docker-build:
	docker build -t projectvital:latest .

## docker-run-dev: Run the Docker container locally with 'dev' profile
docker-run-dev:
	docker run --rm -p 8080:8080 --dns 8.8.8.8 --env-file .env -e SPRING_PROFILES_ACTIVE=dev projectvital:latest

## docker-run-prod: Run the Docker container locally with 'prod' profile
docker-run-prod:
	docker run --rm -p 8080:8080 --dns 8.8.8.8 --env-file .env -e SPRING_PROFILES_ACTIVE=prod projectvital:latest

## up-dev: Build JAR, build Docker, and run in 'dev' environment
up-dev: build docker-build docker-run-dev

## up-prod: Build JAR, build Docker, and run in 'prod' environment
up-prod: build docker-build docker-run-prod

## gcp-setup: Create Artifact Registry repository
gcp-setup:
	gcloud artifacts repositories create $(GCP_REPO) \
		--repository-format=docker \
		--location=$(GCP_REGION) \
		--project=$(GCP_PROJECT_ID)

## gcp-build: Build the Docker image for linux/amd64 (GCP compatible)
gcp-build:
	docker build --platform linux/amd64 -t $(GCP_IMAGE):latest .

## gcp-push: Push the image to Artifact Registry
gcp-push:
	gcloud auth configure-docker $(GCP_REGION)-docker.pkg.dev
	docker push $(GCP_IMAGE):latest

## deploy-dev: Complete deployment pipeline (build + push + deploy) for dev
deploy-dev:
	@$(MAKE) deploy ENV=dev

## deploy-prod: Complete deployment pipeline (build + push + deploy) for prod
deploy-prod:
	@$(MAKE) deploy ENV=prod

## gcp-deploy: Deploy to Cloud Run (legacy - please use deploy-dev or deploy-prod)
gcp-deploy:
	@echo "⚠️  WARNING: 'make gcp-deploy' is legacy. Did you remember to pass ENV? (Defaulting to ENV=$(ENV))"
	@echo "💡 TIP: Use 'make deploy-dev' or 'make deploy-prod' for explicit targets."
ifeq ($(ENV),prod)
	@echo "Deploying to production..."
	gcloud run deploy $(GCP_APP_NAME) \
		--image $(GCP_IMAGE):latest \
		--platform managed \
		--region $(GCP_REGION) \
		--project=$(GCP_PROJECT_ID) \
		--allow-unauthenticated \
		--set-env-vars "SPRING_PROFILES_ACTIVE=$(ENV)" \
		--set-secrets "DATABASE_URL=DATABASE_URL:latest,DATABASE_USERNAME=DATABASE_USERNAME:latest,DATABASE_PASSWORD=DATABASE_PASSWORD:latest,SUPABASE_KEY=SUPABASE_KEY:latest"
else
	@echo "Deploying to dev..."
	gcloud run deploy $(GCP_APP_NAME) \
		--image $(GCP_IMAGE):latest \
		--platform managed \
		--region $(GCP_REGION) \
		--project=$(GCP_PROJECT_ID) \
		--allow-unauthenticated \
		--set-env-vars "SPRING_PROFILES_ACTIVE=$(ENV),DATABASE_URL=$$(grep DATABASE_URL .env | cut -d= -f2- | sed 's/,/\\,/g'),DATABASE_USERNAME=$$(grep DATABASE_USERNAME .env | cut -d= -f2-),DATABASE_PASSWORD=$$(grep DATABASE_PASSWORD .env | cut -d= -f2-),SUPABASE_KEY=$$(grep SUPABASE_KEY .env | cut -d= -f2-)"
endif

## deploy: Complete deployment pipeline (build + push + deploy) to GCP (usage: make deploy ENV=prod)
deploy: gcp-build gcp-push gcp-deploy

## help: Show this help message
help:
	@echo "Usage: make [target]"
	@echo ""
	@echo "Targets:"
	@grep -E '^##' Makefile | sed -e 's/## //' | column -t -s ':'
