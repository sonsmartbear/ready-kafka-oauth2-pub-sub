package com.smartbear.ready.kafka.oauth2.client;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class ProducerOAuth {
    private static final Logger log = LoggerFactory.getLogger(ProducerOAuth.class);

    public static String ACCESS_TOKEN = "";

    public static void main(String[] args) {
        if(args.length == 1) {
            ProducerOAuth.ACCESS_TOKEN = args[0];
        }

        String topic = "default_topic";
        ProducerOAuth producerOAuth = new ProducerOAuth();
        producerOAuth.produceMessage(topic);
    }

    public void produceMessage(String topic) {
        Properties properties = generateConfigureProperties();
        Producer<String, String> producer = new KafkaProducer<>(properties);

        while(true) {
            Scanner sc = new Scanner(System.in);
            System.out.print("Enter a string: ");
            String str = sc.nextLine().trim();
            if(str.equalsIgnoreCase("")) {
                continue;
            }
            if(str.equalsIgnoreCase("exit") || str.equalsIgnoreCase("quit")) {
                log.info("Bye bye!");
                break;
            }
            long millis = System.currentTimeMillis();
            try {
                producer.send(new ProducerRecord<>(topic, millis + "", str));
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }
        producer.close();
    }

    public void publishSingleMessage(String topic, String key, String value) {
        Properties properties = generateConfigureProperties();
        Producer<String, String> producer = new KafkaProducer<>(properties);
        try {
            producer.send(new ProducerRecord<>(topic, key, value));
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            producer.close();
        }
    }

    private Properties generateConfigureProperties(){
        Properties properties = new Properties();

        // kafka bootstrap server
        String bootstrapServer = (String) getEnvironmentVariables(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer);
//        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());

        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        // producer acks
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "all");
        properties.setProperty(ProducerConfig.RETRIES_CONFIG, "3");
        properties.setProperty(ProducerConfig.LINGER_MS_CONFIG, "1");

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
