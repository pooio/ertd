DROP TABLE IF EXISTS `goods_data`;
CREATE TABLE `goods_data` (`ID` Varchar(255) NOT NULL,`DELETED` decimal(1, 0) NULL DEFAULT NULL,`Name` Varchar(255)  NULL DEFAULT NULL, 
`goods_name` varchar(255),`collection_quantity` int,`like_quantity` int,`goods_id` varchar(255),`view_quantity` int,`sale_quantity` int, PRIMARY KEY (`ID`) );

