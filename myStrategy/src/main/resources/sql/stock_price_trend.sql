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

 Date: 14/08/2025 05:26:26
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for stock_price_trend
-- ----------------------------
DROP TABLE IF EXISTS `stock_price_trend`;
CREATE TABLE `stock_price_trend`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `stock_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '股票代码',
  `stock_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '股票名称',
  `industry` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属行业',
  `month1Rate` decimal(10, 2) NULL DEFAULT NULL,
  `month2Rate` decimal(10, 2) NULL DEFAULT NULL,
  `month3Rate` decimal(10, 2) NULL DEFAULT NULL,
  `month4Rate` decimal(10, 2) NULL DEFAULT NULL,
  `month5Rate` decimal(10, 2) NULL DEFAULT NULL,
  `month6Rate` decimal(10, 2) NULL DEFAULT NULL,
  `month7Rate` decimal(10, 2) NULL DEFAULT NULL,
  `month8Rate` decimal(10, 2) NULL DEFAULT NULL,
  `month9Rate` decimal(10, 2) NULL DEFAULT NULL,
  `month10Rate` decimal(10, 2) NULL DEFAULT NULL,
  `monthRate` decimal(10, 4) NULL DEFAULT NULL,
  `yearRate` decimal(10, 4) NULL DEFAULT NULL,
  `last3mRate` decimal(10, 4) NULL DEFAULT NULL,
  `percent` decimal(10, 2) NULL DEFAULT NULL COMMENT '今日涨幅(%)',
  `percent5` decimal(10, 2) NULL DEFAULT NULL COMMENT '五日涨幅(%)',
  `percent10` decimal(10, 2) NULL DEFAULT NULL COMMENT '十日涨幅(%)',
  `percent15` decimal(10, 2) NULL DEFAULT NULL COMMENT '十五日涨幅(%)',
  `percent20` decimal(10, 2) NULL DEFAULT NULL COMMENT '二十日涨幅(%)',
  `percent25` decimal(10, 2) NULL DEFAULT NULL,
  `percent30` decimal(10, 2) NULL DEFAULT NULL COMMENT '三十日涨幅(%)',
  `percent35` decimal(10, 2) NULL DEFAULT NULL,
  `percent40` decimal(10, 2) NULL DEFAULT NULL,
  `percent45` decimal(10, 2) NULL DEFAULT NULL,
  `percent50` decimal(10, 2) NULL DEFAULT NULL,
  `percent60` decimal(10, 2) NULL DEFAULT NULL,
  `percent70` decimal(10, 4) NULL DEFAULT NULL,
  `percent90` decimal(10, 4) NULL DEFAULT NULL,
  `percent100` decimal(10, 4) NULL DEFAULT NULL,
  `percent120` decimal(10, 4) NULL DEFAULT NULL,
  `percent150` decimal(10, 4) NULL DEFAULT NULL,
  `percent170` decimal(10, 4) NULL DEFAULT NULL,
  `percent190` decimal(10, 4) NULL DEFAULT NULL,
  `percent200` decimal(10, 4) NULL DEFAULT NULL,
  `percent230` decimal(10, 4) NULL DEFAULT NULL,
  `percent250` decimal(10, 4) NULL DEFAULT NULL,
  `trade_date5` date NULL DEFAULT NULL COMMENT '五日起始日期(今日倒5个交易日)',
  `trade_date10` date NULL DEFAULT NULL COMMENT '十日起始日期(今日倒10个交易日)',
  `trade_date15` date NULL DEFAULT NULL COMMENT '十五日起始日期(今日倒15个交易日)',
  `trade_date20` date NULL DEFAULT NULL COMMENT '二十日起始日期(今日倒20个交易日)',
  `trade_date25` date NULL DEFAULT NULL,
  `trade_date30` date NULL DEFAULT NULL COMMENT '三十日起始日期(今日倒30个交易日)',
  `trade_date35` date NULL DEFAULT NULL,
  `trade_date40` date NULL DEFAULT NULL,
  `trade_date45` date NULL DEFAULT NULL,
  `trade_date50` date NULL DEFAULT NULL,
  `trade_date60` date NULL DEFAULT NULL,
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `month11Rate` decimal(10, 2) NULL DEFAULT NULL,
  `month12Rate` decimal(10, 2) NULL DEFAULT NULL,
  `percent2` decimal(10, 2) NULL DEFAULT NULL,
  `percent3` decimal(10, 2) NULL DEFAULT NULL,
  `percent4` decimal(10, 2) NULL DEFAULT NULL,
  `percent6` decimal(10, 2) NULL DEFAULT NULL,
  `percent7` decimal(10, 2) NULL DEFAULT NULL,
  `percent8` decimal(10, 2) NULL DEFAULT NULL,
  `percent9` decimal(10, 2) NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_stock_code`(`stock_code` ASC) USING BTREE,
  INDEX `idx_industry`(`industry` ASC) USING BTREE,
  INDEX `idx_percent`(`percent` ASC) USING BTREE,
  INDEX `idx_percent30`(`percent30` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 218001 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '个股涨幅统计表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
