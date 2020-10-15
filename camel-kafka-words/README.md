The application has two parts

- A producer which sends random words to a Kafka topic
- A consumer which receives the messages from the Kafka topic

#### Preparing Kafka broker

From Apache Kafka (http://kafka.apache.org/quickstart) you download Kafka and follow the instructions to run Kafka.

    tar xf ~/Downloads/kafka_2.12-0.11.0.1.tgz
    cd kafka_2.12-0.11.0.1
     
Then you start Zookeeper
     
    bin/zookeeper-server-start.sh config/zookeeper.properties
    
And from another shell you start Kafka
    
    bin/kafka-server-start.sh config/server.properties


#### Manage topic

    .\kafka-topics.bat --create --topic words2  --replication-factor 1 --partitions 2 --bootstrap-server localhost:9092 
    .\kafka-topics.bat --describe --topic words2  --bootstrap-server localhost:9092
	.\kafka-run-class.bat kafka.tools.GetOffsetShell --broker-list localhost:9092 --topic words2 --time -1	

#### Create Words db schema

    see src\db\Words.sql
	and src\db\UpsertEvent.sql
       
### Running the producer

When Kafka is up and running you can start the producer which sends messages to Kafka

    mvn compile exec:java -P producer
    
### Running the consumer

In another shell you can run the foo consumer which will receive the messages that the producer sends

    mvn compile exec:java -P foo
   
### Kill a consumer

    jps -m
    taskkill /PID <id-from-above> /F

### Failover the consumer

You can scale up the consumers by starting a new consumer from a shell

    mvn compile exec:java -P bar
	
### Check db

    see src\db\Queries.sql  	

