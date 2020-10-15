package ld.lab.camel.kafka.consumer;

import org.apache.camel.main.Main;

public class BarApp {

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.configure().addRoutesBuilder(new KafkaRcvRoute("Bar"));
        main.run();
    }
}
