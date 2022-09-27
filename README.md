# Kafka OAuth 2.0

## Client X Broker Authentication

### Build the project
To compile java files, please use a command as per below
```
mvn clean dependency:copy-dependencies
```

### Container Environments
For client broker authentication, configure this environment variables:

- OAUTH2_ACCESS_TOKEN: use Postman or any tools to get an access token

For Mac

- export OAUTH2_ACCESS_TOKEN=<ACCESS_TOKEN>

For Window

- set OAUTH2_ACCESS_TOKEN=<ACCESS_TOKEN>

### Start Publisher and Consumer
Go to target folder and execute the command below to start the publisher
