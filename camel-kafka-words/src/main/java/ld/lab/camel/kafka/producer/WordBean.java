package ld.lab.camel.kafka.producer;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Random;

public class WordBean {

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    private String[] words = new String[]{
        "Camel",
        "Rocks",
        "Whiskey",
        "Beer",
        "Bad",
        "Donkey",
        "Cool",
        "Dude",
        "Hawt",
        "Fabric8"
    };

    private int counter;

    public String generateWord() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String tsStr = sdf.format(timestamp);
        int ran = new Random().nextInt(words.length);

        return "#" + ++counter + "*" + words[ran] + "@" + tsStr;
    }
}
