#!/bin/bash

# Create sample queue
aws --endpoint-url=http://localhost:4566 sqs create-queue --queue-name sample-queue --region us-east-1
