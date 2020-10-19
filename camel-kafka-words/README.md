The application has two parts

- A producer which sends #uid*word@timestamp formatted events to a Kafka topic
- A consumer which receives the messages from the Kafka topic and updates a Microsoft SQL Server table

#### Preparing Kafka broker

From Apache Kafka (http://kafka.apache.org/quickstart) you download Kafka and follow the instructions to run Kafka.

    tar xf ~/Downloads/kafka_2.12-0.11.0.1.tgz
    cd kafka_2.12-0.11.0.1
     
Then you start Zookeeper
     
    bin/zookeeper-server-start.sh config/zookeeper.properties
    
And from another shell you start Kafka
    
    bin/kafka-server-start.sh config/server.properties


#### Manage Kafka topic

To spread the consumer load across a number of processes in a consumer group, the number of topic partitions must be greater or equal to the number of partitions.  

    .\kafka-topics.bat --create --topic words2  --replication-factor 1 --partitions 2 --bootstrap-server localhost:9092 
    .\kafka-topics.bat --describe --topic words2  --bootstrap-server localhost:9092
	.\kafka-run-class.bat kafka.tools.GetOffsetShell --broker-list localhost:9092 --topic words2 --time -1	

### Preparing Microsoft SQL Server

If you don't already have an SQL Server installation, you can download 2019 Developer 
or Express editions for free from https://www.microsoft.com/en-gb/sql-server/sql-server-downloads. In addition, 
you'll need a database query tool. I've used Visual Studio code which is also free and has got an SQL Server extension.

In order to connect from Camel you'll need Microsoft JDBC driver from 
https://docs.microsoft.com/en-us/sql/connect/jdbc/download-microsoft-jdbc-driver-for-sql-server?view=sql-server-ver15  
This contains mssql-jdbc_auth-8.4.1.x64.dll wich you'll need to copy to your JDK bin folder (e.g. C:\Program Files\AdoptOpenJDK\openjdk-11.0.8_10\bin).


#### Create Words db schema

Once  you got an SQL Server instance installed on you machine use the SQL files below to setup the database part:

    src\db\Words.sql
	src\db\UpsertEvent.sql
       
### Running the producer

When Kafka is up and running you can start the producer which sends messages to Kafka

    mvn compile exec:java -P producer
    
### Running the consumer

In another shell you can run the foo consumer which will receive the messages that the producer sends

    mvn compile exec:java -P foo
   
### Kill a consumer

In order to simulate a node down failure scenario you can use the commands below.

    jps -m
    taskkill /PID <id-from-above> /F

### Failover the consumer

You can scale up the consumers by starting a new consumer from a shell

    mvn compile exec:java -P bar
	
### Check db

You can visualize the events through the data pipeline and check throughput/latency from producer to database using the following SQL file:

    src\db\Queries.sql  	

