package ld.lab.camel.kafka.producer;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.component.kafka.KafkaConfiguration;

import java.util.Random;

public class WordRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        // lets shutdown quicker
        getContext().getShutdownStrategy().setTimeout(10);

        // configure the kafka component to use the broker
        KafkaComponent kafka = new KafkaComponent();

        // you can specify more brokers separated by comma
        KafkaConfiguration kCfg = new KafkaConfiguration();
        kCfg.setBrokers("localhost:9092");
        kafka.setConfiguration(kCfg);

        // add component to CamelContext
        getContext().addComponent("kafka", kafka);

        // use a timer to trigger every x milliseconds and generate an #uid*word@timestamp event
        from("timer:producer?period=1000")//inject 1 events per second
            .bean(new WordBean())
                .setHeader("kafka.KEY", body().regexReplaceAll("#\\d+\\*","").regexReplaceAll("@.+",""))
                .to("kafka:words2")
                .to("log:words?groupInterval=1000");
    }
}
