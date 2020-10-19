CREATE DATABASE mydb
GO

USE mydb;
DROP TABLE [dbo].[Words]
GO

CREATE TABLE Words
(
    EventUID int,
    Word char(100),
    Consumer char(10),
    TpPrt int,
    Offset int,
    IsDuplicate bit,
    ConsumedAt datetime2 DEFAULT(getdate()),
    ProducedAt datetime2
);
GO