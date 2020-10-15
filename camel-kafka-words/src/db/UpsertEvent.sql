USE mydb
GO

DROP PROCEDURE UpsertEvent
GO

CREATE PROCEDURE UpsertEvent
    @uid int,
    @word nvarchar(100),
    @consumer nvarchar(10),
    @tpPrt int,
    @offset int,
    @producedAt datetime2
AS

BEGIN TRANSACTION
IF (SELECT count(*)
FROM Words
WHERE EventUID=@uid) > 0
        UPDATE Words SET [ConsumedAt] = GETDATE(), IsDuplicate=1
        WHERE EventUID = @uid
    ELSE
        INSERT INTO Words
    (EventUID, Word, Consumer, TpPrt, Offset, IsDuplicate, ProducedAt)
VALUES
    (@uid, @word, @consumer, @tpPrt, @offset, 0, @producedAt)
COMMIT