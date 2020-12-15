DROP TABLE IF EXISTS `goods_labels`;
CREATE TABLE `goods_labels` (`ID` Varchar(255) NOT NULL,`DELETED` decimal(1, 0) NULL DEFAULT NULL,`Name` Varchar(255)  NULL DEFAULT NULL, 
`label_type` varchar(255),`create_user` varchar(255), PRIMARY KEY (`ID`) );

