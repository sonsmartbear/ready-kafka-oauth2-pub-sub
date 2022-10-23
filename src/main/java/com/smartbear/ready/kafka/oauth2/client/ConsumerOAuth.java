package com.smartbear.ready.kafka.oauth2.client;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Properties;

public class ConsumerOAuth {
    Logger logger = LoggerFactory.getLogger(ConsumerOAuth.class);

    public static void main(String[] args) {
        ConsumerOAuth consumer = new ConsumerOAuth();
        consumer.consumeMessage("default_topic");
    }

    public void consumeMessage(String topic) {
        System.out.println("Start consume message");
        System.out.println(topic);
        Properties properties = generateConfigureProperties();
        Consumer<String, String> consumer = new KafkaConsumer<>(properties);
        consumer.subscribe(Arrays.asList(topic));
        while (true) {
            try {
                ConsumerRecords<String, String> records = consumer.poll(100);
                for (ConsumerRecord<String, String> record : records) {
                    logger.info("Key: {} && Value: {}", record.key(), record.value());
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
        consumer.close();
    }
    private Properties generateConfigureProperties(){
        Properties properties = new Properties();

        // kafka bootstrap server
        String bootstrapServer = (String) getEnvironmentVariables(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // kafka consumer props
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "group-id");
        properties.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1);
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");

        //kafka oauth configuration
        properties.setProperty("sasl.jaas.config", "org.apache.kafka.common.security.oauthbearer.OAuthBearerLoginModule required ;");
        properties.setProperty("security.protocol", "SASL_PLAINTEXT");
        properties.setProperty("sasl.mechanism", "OAUTHBEARER");
        properties.setProperty("sasl.login.callback.handler.class", "com.smartbear.ready.kafka.oauth2.config.OauthAuthenticateLoginCallbackHandler");

        return properties;
    }

    private String getEnvironmentVariables(String envName, String defaultValue) {
        String result= System.getenv(envName);
        if(result == null){
            result = defaultValue;
        }
        return result;
    }

}
