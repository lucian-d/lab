package ld.lab.camel.kafka.consumer;

import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import org.apache.camel.Message;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaComponent;
import org.apache.camel.component.kafka.KafkaConfiguration;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.component.kafka.KafkaManualCommit;
import org.apache.camel.component.sql.stored.SqlStoredComponent;
//import org.graalvm.compiler.word.Word;

import java.util.Random;

public class KafkaRcvRoute extends RouteBuilder {

    String _name;
    int _counter = 0;
    int _posInBatch = 1;
    Boolean _failAt147 = false;

    public KafkaRcvRoute(String name) {
        _name = name;
    }

    public int key() {
        int ran = new Random().nextInt(2);
        return ran;
        //return String.valueOf(ran);
    }

    @Override
    public void configure() throws Exception {
        // lets shutdown quicker
        getContext().getShutdownStrategy().setTimeout(10);

        // configure the kafka component to use the broker
        var kafka = new KafkaComponent();
        // you can specify more brokers separated by comma
        var config = new KafkaConfiguration();
        config.setBrokers("localhost:9092");
        kafka.setConfiguration(config);
        //kafka.setBrokers("localhost:9092");

        // add component to CamelContext
        getContext().addComponent("kafka", kafka);

        // configure SqlStored component to use our MS SQL server database
        SqlStoredComponent sqlStoredProc = new SqlStoredComponent();
        // prepare a JDBC data source
        // Server=localhost\SQLEXPRESS01;Database=master;Trusted_Connection=True;
        SQLServerDataSource ds = new SQLServerDataSource();
        ds.setServerName("localhost");
        ds.setInstanceName("SQLEXPRESS01");
        ds.setDatabaseName("mydb");
        //ds.setSSLProtocol("TLSv1.2");
        // For line below to work
        // mssql-jdbc_auth-8.4.1.x64.dll must be present in  C:\Program Files\AdoptOpenJDK\jdk8u265-b01\jre\bin
        ds.setIntegratedSecurity(true);
        // reference data source from Camel
        sqlStoredProc.setDataSource(ds);
        getContext().addComponent("mssql-sp", sqlStoredProc);

        ConsumeWithAtLeastOnceAndManualCommit();
        //TestUpsertEventSqlProc();
    }


    /*
    Kafka consumer params:
    maxPollRecords:     The maximum number of records returned in a single call to poll()
    autoOffsetReset:    What to do when there is no initial offset in ZooKeeper or if an offset is out of range:
                        earliest : automatically reset the offset to the earliest offset
                        latest : automatically reset the offset to the latest offset
                        fail: throw exception to the consumer.
    autoCommitEnable:   If true, periodically commit to ZooKeeper the offset of messages already fetched by the consumer.
                        This committed offset will be used when the process fails as the position
                        from which the new consumer will begin
    allowManualCommit:  Whether to allow doing manual commits via KafkaManualCommit. If this option is enabled
                        then an instance of KafkaManualCommit is stored on the Exchange message header,
                        which allows end users to access this API and perform manual offset commits via the Kafka consumer.
    breakOnFirstError:  This options controls what happens when a consumer is processing an exchange and it fails.
                        If the option is false then the consumer continues to the next message and processes it.
                        If the option is true then the consumer breaks out, and will seek back to offset of the message
                        that caused a failure, and then re-attempt to process this message. However this can lead to
                        endless processing of the same message if its bound to fail every time, eg a poison message.
                        Therefore its recommended to deal with that for example by using Camel’s error handler.

    Kafka consumer headers:
    KafkaConstants.LAST_RECORD_BEFORE_COMMIT:   Whether or not it’s the last record before commit
                                                (only available if autoCommitEnable endpoint parameter is false)
    KafkaConstants.LAST_POLL_RECORD:            Indicates the last record within the current poll request
                                                (only available if autoCommitEnable endpoint parameter is false or allowManualCommit is true)
    */
    private void ConsumeWithAtLeastOnceAndManualCommit() {
        from("kafka:words2?groupId=mygroup&maxPollRecords=50&autoOffsetReset=earliest&autoCommitEnable=false&allowManualCommit=true&breakOnFirstError=true")
                .routeId(_name)
                .process(exchange -> {
                    String body = exchange.getIn().getBody().toString();

                    // body::= #uid*word@ts
                    String[] parts = body.split("[#*@]");

                    Message msg = exchange.getIn();
                    msg.setHeader("eventUID", parts[1]);
                    msg.setHeader("word", parts[2]);
                    msg.setHeader("producedAt", parts[3]);
                    msg.setHeader("consumer", _name);
                    msg.setHeader("PiB", _posInBatch);

                    _counter++;
                    _posInBatch++;
                })
                .log(_name +
                        " got word ${body}; topic ${headers[kafka.TOPIC]}; prt# ${headers[kafka.PARTITION]};"
                        + " offset ${headers[kafka.OFFSET]}; key ${headers[kafka.KEY]}; UID ${headers[eventUID]};"
                        + " LRBC ${headers[kafka.LAST_RECORD_BEFORE_COMMIT]}; LPR ${headers[kafka.LAST_POLL_RECORD]}; PiB ${headers[PiB]};" )
                //.log("Executing UpsertEvent stored proc with eventUID=${headers[eventUID]} & word=${headers[word]}")
                .to("mssql-sp:[dbo].[UpsertEvent](VARCHAR ${headers[eventUID]}, VARCHAR ${headers[word]}, "
                        +"VARCHAR ${headers[consumer]}, VARCHAR ${headers[kafka.PARTITION]}, "
                        +"VARCHAR ${headers[kafka.OFFSET]}, VARCHAR ${headers[producedAt]})")
                .process(exchange -> {
                    // optional code to simulate db/processing failures
                    if ((_counter == 147) && (_failAt147))
                    {
                        _counter = 0;
                        // On exception, KafkaConsumer will commit offset to current position - 1
                        // so that the next poll() will first get the message that failed.
                        throw new Exception("Forced error before offset commit. Last event shall be reprocessed and handled as duplicate.");
                    }

                    // manually commit offset if it is last message in batch
                    Boolean lastOne = exchange.getIn().getHeader(KafkaConstants.LAST_RECORD_BEFORE_COMMIT, Boolean.class);

                    if (lastOne) {
                        _posInBatch = 1;
                        KafkaManualCommit manual =
                                exchange.getIn().getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class);
                        if (manual != null) {
                            //System.out.println("manually committing the offset for batch");
                            manual.commitSync();
                        }
                    }
                });
    }

    private void TestUpsertEventSqlProc() {
        from("timer://foo?fixedRate=true&period=5000")//every 5 secs
                .process(e -> {e.getIn().setHeader("eventUID", 12); e.getIn().setHeader("word", "camel");})
                .log("Executing UpsertEvent stored proc with eventUID=${headers[eventUID]} & word=${headers[word]}")
                .to("mssql-sp:[dbo].[UpsertEvent](VARCHAR ${headers[eventUID]}, VARCHAR ${headers[word]})");
    }

    // Exactly once delivery, i.e. offset control on consumer side implies:
    // a) consumer stores current kafka topic offset in db, and
    // b) before fetching events, consumer seeks topic position to current offset; unclear if Camel supports it;
    // this option has been dropped in favour of "at least once delivery" with idempotent db operation.
    private void ConsumeWithOffsetCtrl() {
        // use a timer to trigger every 100 milli seconds and generate a random word
        // which is sent to kafka
        from("kafka:words2?groupId=mygroup&maxPollRecords=3&autoOffsetReset=earliest&autoCommitEnable=false&allowManualCommit=true&breakOnFirstError=true")
                .routeId("Foo [Java DSL]")
                .log("Got word ${body}; topic ${headers[kafka.TOPIC]}; partition ${headers[kafka.PARTITION]}; offset ${headers[kafka.OFFSET]}; key ${headers[kafka.KEY]}")
                .process(exchange -> {
                    // set headers[myEventUID]
                    System.out.println("processing exchange# " + exchange.getExchangeId());
                })
                .to("file:out")
                .process(exchange -> {
                    // manually commit offset if it is last message in batch
                    KafkaManualCommit manual =
                            exchange.getIn().getHeader(KafkaConstants.MANUAL_COMMIT, KafkaManualCommit.class);
                    if (manual != null) {
                        System.out.println("manually committing the offset for batch");
                        manual.commitSync();
                    }
                });
    }
}
