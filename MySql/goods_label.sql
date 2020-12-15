DROP TABLE IF EXISTS `goods_label`;
CREATE TABLE `goods_label` (`ID` Varchar(255) NOT NULL,`DELETED` decimal(1, 0) NULL DEFAULT NULL,`Name` Varchar(255)  NULL DEFAULT NULL, 
`goods_name` varchar(255),`labels_name` varchar(255),`labels_id` varchar(255),`goods_id` varchar(255), PRIMARY KEY (`ID`) );

