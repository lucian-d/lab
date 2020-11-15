package ld.lab.camel.kafka.producer;

import org.apache.camel.main.Main;

public class WordGenApp {
    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.configure().addRoutesBuilder(new WordRoute());
        main.run(args);
    }
}
