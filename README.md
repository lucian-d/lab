#camel-sql-pymts example prerequisites:
- you'll need Apache Maven and Karaf installed 
- download camel-sql-pymts code locally

#Compile camel-sql-pymts example and install in Maven:
- and open a command promt to directory where pom.xml lives 
- run mvn clean install
- wait till the code compiles, unit tests run and artifacts are deployed to you local Maven repository

#Deploy camel-sql-pymts example to Karaf:
- open a Karaf console and run the following commands
- karaf@root()> feature:repo-add mvn:ld.lab.camel.sql/pymts-dataflow/1.0-SNAPSHOT/xml/features
- karaf@root()> feature:install pymts-dataflow

#Test camel-sql-pymts example:
- run karaf@root()> log:display
- you should get:
16:50:54.900 INFO [Camel (payments-data-flow) thread #2 - timer://foo] Inserted into db new payment #184.
16:50:55.244 INFO [Camel (payments-data-flow) thread #1 - sql://select%20*%20from%20payments%20where%20processed%20=%20false] Processed one database record; result message: Payment #184 from Joe to Ana of 51GBP IS fraud.
16:50:59.903 INFO [Camel (payments-data-flow) thread #2 - timer://foo] Inserted into db new payment #185.
16:51:00.315 INFO [Camel (payments-data-flow) thread #1 - sql://select%20*%20from%20payments%20where%20processed%20=%20false] Processed one database record; result message: Payment #185 from Bob to Tim of 4GBP IS NOT fraud.

#Check camel-sql-pymts example:
- run karaf@root()> jdbc:ds-list
- you should get:
Name             | Service Id | Product      | Version               | URL                        | Status
-----------------+------------+--------------+-----------------------+----------------------------+-------
jdbc/ds-payments | 291        | Apache Derby | 10.14.2.0 - (1828579) | jdbc:derby:memory:dbpymts  | OK
- run karaf@root()> jndi:names
- you should get:
JNDI Name                     | Class Name
------------------------------+-----------------------------------------------
osgi:service/jdbc/ds-payments | org.apache.derby.jdbc.EmbeddedDataSource
