package local_stream_processor.sink;


import local_stream_processor.functions.SinkFunction;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.Properties;

public class LSPKafkaProducer<T> implements SinkFunction<T> {
    private KafkaProducer<String, String> kafkaProducer;
    private String topic;
    private String server;

    public LSPKafkaProducer(String server, String topic){
        Properties properties = new Properties();
        properties.put("bootstrap.servers", server);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        this.kafkaProducer = new KafkaProducer<String, String>(properties);

        this.topic = topic;
        this.server = server;
    }

    public void process(T element){
        this.kafkaProducer.send(new ProducerRecord<>(topic, element.toString()));
    }

    public void finalize(){
        System.out.println("END");
        this.kafkaProducer.close();
    }

    @Override
    public String toString() {
        return "LSP Kafka Producer\nTopic: " + topic + "\nServer Address: " + server;
    }
}

