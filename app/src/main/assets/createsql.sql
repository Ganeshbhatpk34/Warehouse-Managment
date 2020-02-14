CREATE TABLE IF NOT EXISTS `dummyTable` (
    `ID` int DEFAULT '1',
    `SerialKey` varchar(20) DEFAULT NULL,
    `Usename` varchar(20) DEFAULT NULL,
    `Config` varchar(20) DEFAULT "i",
    `IP` varchar(20) DEFAULT "",
    `LastModifiedOn` datetime DEFAULT CURRENT_TIMESTAMP
  );

INSERT INTO dummyTable(SerialKey,LastModifiedOn,Usename) VALUES ("",datetime('now','localtime'),"");