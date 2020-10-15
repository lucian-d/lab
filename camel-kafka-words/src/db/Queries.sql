USE mydb
GO

SELECT Consumer, TpPrt, COUNT(*) EvtsConsumed, 
MIN(Offset) MinOffset, MAX(Offset) MaxOffset,
COUNT(*)/DATEDIFF(second, MIN([ConsumedAt]), MAX([ConsumedAt])) EvtsPerSec,
AVG(DATEDIFF(second, ProducedAt, ConsumedAt)) AvgP2CLtncySec
FROM [mydb].[dbo].[Words]
GROUP BY Consumer, TpPrt

SELECT COUNT(*) Duplicates 
FROM [mydb].[dbo].[Words]
WHERE IsDuplicate=1

SELECT count(EventUID) OutOfSeqUID --<-- consumers inbalance
FROM [mydb].[dbo].[Words] 
WHERE EventUID + 1 NOT IN (SELECT EventUID FROM [mydb].[dbo].[Words])

SELECT TOP (100) *
  FROM [mydb].[dbo].[Words]
  --WHERE TpPrt=1
  ORDER BY EventUID DESC
 
--DELETE FROM [mydb].[dbo].[Words]