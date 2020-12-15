DROP TABLE IF EXISTS `goods`;
CREATE TABLE `goods` (`ID` Varchar(255) NOT NULL,`DELETED` decimal(1, 0) NULL DEFAULT NULL,`Name` Varchar(255)  NULL DEFAULT NULL, 
`create_date` varchar(255),`datas_id` varchar(255),`datas_name` varchar(255),`price` decimal(18,2),`goods_type` varchar(255),`status` varchar(255), PRIMARY KEY (`ID`) );

