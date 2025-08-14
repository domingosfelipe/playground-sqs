<p align="left">
  <a href="https://github.com/domingosfelipe/playground-sqs/actions/workflows/ci.yml?branch=main">
    <img alt="GitHub CI Status" src="https://img.shields.io/github/actions/workflow/status/domingosfelipe/playground-sqs/ci.yml?logo=github&label=CI&style=flat">
  </a>
  <a href="https://codecov.io/github/domingosfelipe/playground-sqs" > 
    <img alt="Codecov Status" src="https://codecov.io/github/domingosfelipe/playground-sqs/graph/badge.svg?token=53KWKPNJG8"/> 
  </a>  
  <a href="https://app.circleci.com/pipelines/github/domingosfelipe/playground-sqs?branch=main">
    <img alt="CircleCI Status" src="https://img.shields.io/circleci/build/github/domingosfelipe/playground-sqs/main?logo=circleci&label=CircleCI&style=flat">
  </a>
</p>

--- 

# Playground SQS

A sample project demonstrating how to use **AWS SQS** locally with **LocalStack**, built with **Java 21** and **Gradle**.

> [!CAUTION]
> This repository is provided for study purposes only. No warranty or support is offered.

## Requirements

- Docker
- Java 21
- Gradle

## Run Locally

1. Start LocalStack container with the sample SQS queue:

	  ```shell
		docker-compose up -d
		```

2. Build the project and run all tests using Gradle:

	  ```shell
		./gradlew build
		```

3. Run the Spring Boot application:

	  ```shell
		./gradlew bootRun
		```

4. The project is now running locally :tada:

> [!TIP]
> Use [LocalStack](https://app.localstack.cloud/sign-in) website to manage all project queues
