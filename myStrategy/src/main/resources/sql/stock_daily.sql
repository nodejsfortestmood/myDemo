/*
 Navicat Premium Dump SQL

 Source Server         : mybatis
 Source Server Type    : MySQL
 Source Server Version : 80039 (8.0.39)
 Source Host           : localhost:3306
 Source Schema         : stock

 Target Server Type    : MySQL
 Target Server Version : 80039 (8.0.39)
 File Encoding         : 65001

 Date: 14/08/2025 05:26:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for stock_daily
-- ----------------------------
DROP TABLE IF EXISTS `stock_daily`;
CREATE TABLE `stock_daily`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `stock_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '股票代码',
  `trade_date` date NOT NULL COMMENT '交易日期',
  `open_price` decimal(10, 4) NULL DEFAULT NULL COMMENT '开盘价',
  `close_price` decimal(10, 4) NULL DEFAULT NULL COMMENT '收盘价',
  `high_price` decimal(10, 4) NULL DEFAULT NULL COMMENT '最高价',
  `low_price` decimal(10, 4) NULL DEFAULT NULL COMMENT '最低价',
  `chg` decimal(10, 4) NULL DEFAULT NULL COMMENT '涨跌额',
  `turnoverrate` decimal(10, 4) NULL DEFAULT NULL COMMENT '换手率',
  `percent` decimal(10, 4) NULL DEFAULT NULL COMMENT '涨跌幅',
  `volume` bigint NULL DEFAULT NULL COMMENT '成交量(手)',
  `amount` decimal(20, 4) NULL DEFAULT NULL COMMENT '成交额(元)',
  `ma5` decimal(10, 4) NULL DEFAULT NULL COMMENT '5日均线',
  `ma10` decimal(10, 4) NULL DEFAULT NULL COMMENT '10日均线',
  `ma20` decimal(10, 4) NULL DEFAULT NULL COMMENT '20日均线',
  `ma30` decimal(10, 4) NULL DEFAULT NULL COMMENT '30日均线',
  `ma60` decimal(10, 4) NULL DEFAULT NULL COMMENT '60日均线',
  `ma120` decimal(10, 4) NULL DEFAULT NULL COMMENT '120日均线',
  `ma250` decimal(10, 4) NULL DEFAULT NULL COMMENT '250日均线',
  `amt5` decimal(20, 4) NULL DEFAULT NULL,
  `amt10` decimal(20, 4) NULL DEFAULT NULL,
  `amt20` decimal(20, 4) NULL DEFAULT NULL,
  `amt30` decimal(20, 4) NULL DEFAULT NULL,
  `amt60` decimal(20, 4) NULL DEFAULT NULL,
  `turnoverrate5` decimal(10, 4) NULL DEFAULT NULL,
  `turnoverrate10` decimal(10, 4) NULL DEFAULT NULL,
  `turnoverrate20` decimal(10, 4) NULL DEFAULT NULL,
  `turnoverrate30` decimal(10, 4) NULL DEFAULT NULL,
  `turnoverrate60` decimal(10, 4) UNSIGNED ZEROFILL NULL DEFAULT NULL,
  `volume5` decimal(20, 4) NULL DEFAULT NULL,
  `volume10` decimal(20, 4) NULL DEFAULT NULL,
  `volume20` decimal(20, 4) NULL DEFAULT NULL,
  `volume30` decimal(20, 4) NULL DEFAULT NULL,
  `volume60` decimal(20, 4) NULL DEFAULT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `ok` tinyint NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_stock_date`(`stock_code` ASC, `trade_date` ASC) USING BTREE,
  INDEX `idx_trade_date`(`trade_date` ASC) USING BTREE,
  INDEX `idx_ma_cross`(`ma5` ASC, `ma20` ASC, `trade_date` ASC) USING BTREE COMMENT '均线交叉查询优化'
) ENGINE = InnoDB AUTO_INCREMENT = 5255858 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '股票日线表(含均线)' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
