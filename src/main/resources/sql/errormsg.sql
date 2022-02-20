/*
Date: 2022-01-16 20:45:10
*/
SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for emulator_sys_config
-- ----------------------------
DROP TABLE IF EXISTS `airbase_alert_info`;
CREATE TABLE `airbase_alert_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `alert_info` text COMMENT '提示信息',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC;
INSERT INTO `airbaseerror`.`airbase_alert_info` (`id`, `alert_info`) VALUES ('1', 'Database initialization failed! Please check the usage documentation, is the SpringBoot configuration file correct? Does the sql directory exist? Does the sql file exist?');
