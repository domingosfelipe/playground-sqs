# playground-sqs

Study purposes only.

> [!WARNING]
> This repository is for study purposes only. No warranty is provided.

[![pipeline](https://img.shields.io/github/actions/workflow/status/domingosfelipe/playground-sqs/ci.yml?label=pipeline&logo=github)](https://github.com/domingosfelipe/playground-sqs/actions/workflows/ci.yml) [![artifacts](https://img.shields.io/badge/artifacts-attested-brightgreen?logo=github)](https://github.com/domingosfelipe/playground-sqs#artifact-attestation) [![circleci](https://img.shields.io/circleci/build/github/domingosfelipe/playground-sqs/main?label=circleci&logo=circleci&style=flat-square)](https://app.circleci.com/pipelines/github/domingosfelipe/playground-sqs?branch=main)

## Run Locally

1. Run docker compose to create container with LocalStack and `sample-queue`:

    ```shell
    docker-compose up -d
    ```
3. Run gradle build to run all tests and build project:

    ```shell
    gradlew build
    ```
4. Finally, run project:

    ```shell
    gradlew bootRun
    ```
5. All done. Project is running :tada:!

> [!TIP]
> Use [LocalStack](https://app.localstack.cloud/sign-in) website to manage all project queues