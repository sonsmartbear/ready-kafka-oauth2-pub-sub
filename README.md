# Kafka OAuth 2.0

## Client X Broker Authentication

### Build the project
To compile java files, please use a command as per below
```
mvn clean compile dependency:copy-dependencies jar:jar
```

### Get an Access Token by Postman

- Here I already setup a account on Okta server as per below:
```
Id: cong.dang@waverleysoftware.com
Pass: alibaba123
```
- Open th Postman and enter the value as per below:
```
Grant Type: Client Credentials
Access Token URL: https://dev-68831743.okta.com/oauth2/aasokta/v1/token
Client ID: 0oa6kif2kqiQBybKT5d7
Client Secret: 0GYiLcM0V23yV8Kngcd5DvVaeuOxJT8r7hPhQ70e
Scope: kafka
Client Authentication: Send as Basic Header
```

### Container Environments
For client broker authentication, configure this environment variables:

- OAUTH2_ACCESS_TOKEN: use Postman or any tools to get an access token

For Mac

- export OAUTH2_ACCESS_TOKEN=<ACCESS_TOKEN>

For Window

- set OAUTH2_ACCESS_TOKEN=<ACCESS_TOKEN>

### Start Publisher and Consumer
Open 2 terminal window and go to target folder.

- Execute a consumer by command below, remember change path of project folder in command below corresponding to your configuration:
```
java -cp "ready-kafka-oauth2-pub-sub-1.0-SNAPSHOT.jar:/Users/macbook/Docs/pro_intellij/apache_kafka/ready-kafka-oauth2-pub-sub/target/dependency/*" com.smartbear.ready.kafka.oauth2.client.ConsumerOAuth
```

- Clone your project into a other folder, here I clone it into ready-kafka-oauth2-pub-sub-second folder and execute a publisher by command below, remember change path of project folder in command below corresponding to your configuration:
```
java -cp "ready-kafka-oauth2-pub-sub-1.0-SNAPSHOT.jar:/Users/macbook/Docs/pro_intellij/apache_kafka/ready-kafka-oauth2-pub-sub-second/target/dependency/*" com.smartbear.ready.kafka.oauth2.client.ProducerOAuth
```