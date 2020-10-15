package ld.lab.camel.kafka.producer;

import org.apache.camel.main.Main;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Date;

public class WordGenApp {

    public static void main(String[] args) throws Exception {
        //PlayWithDateTime();
        //PlayWithRegex();
        Main main = new Main();
        main.configure().addRoutesBuilder(new WordRoute());
        main.run(args);
    }

    private static void PlayWithRegex() {
        String body = "#uid-word@ts";
        String[] parts = body.split("[#-@]");
        System.out.println(parts[1] + "; " + parts[2] + "; " + parts[3]);
    }

    private static void PlayWithDateTime() throws ParseException {
        //method 1
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        System.out.println("timestamp: " + timestamp);

        //method 2 - via Date
        Date date = new Date();
        System.out.println("date.getTime(): " + new Timestamp(date.getTime()));

        //return number of milliseconds since January 1, 1970, 00:00:00 GMT
        System.out.println("timestamp.getTime(): " + timestamp.getTime());

        //format timestamp
        String strTs = WordBean.sdf.format(timestamp);
        System.out.println("sdf.format(timestamp): " + strTs);

        Date dtFromStr = WordBean.sdf.parse(strTs);
        System.out.println("sdf.parse(strTs): " + new Timestamp(dtFromStr.getTime()));

        /* Output:
        timestamp: 2020-10-07 10:05:21.134
        date.getTime(): 2020-10-07 10:05:21.137
        timestamp.getTime(): 1602061521134
        sdf.format(timestamp): 2020.10.07.10.05.21.134
        sdf.parse(strTs): 2020-10-07 10:05:21.134
         */
    }
}
