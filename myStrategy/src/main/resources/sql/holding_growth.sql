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

 Date: 14/08/2025 05:25:19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for holding_growth
-- ----------------------------
DROP TABLE IF EXISTS `holding_growth`;
CREATE TABLE `holding_growth`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `stock_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '股票代码',
  `stock_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '股票名称',
  `entry_date` date NULL DEFAULT NULL COMMENT '进场点日期',
  `entry_price` decimal(12, 4) NULL DEFAULT NULL COMMENT '进场点日期收盘价',
  `platform_start_date` date NULL DEFAULT NULL COMMENT '平台开始日期',
  `platform_start_price` decimal(12, 4) NULL DEFAULT NULL COMMENT '平台开始日期收盘价',
  `platform_end_date` date NULL DEFAULT NULL COMMENT '平台结束日期',
  `platform_end_price` decimal(12, 4) NULL DEFAULT NULL COMMENT '平台结束日期收盘价',
  `exit_date` date NULL DEFAULT NULL COMMENT '未来出场点日期',
  `exit_price` decimal(12, 4) NULL DEFAULT NULL COMMENT '未来出场点日期收盘价',
  `holding_days` int NULL DEFAULT NULL COMMENT '持有天数',
  `growth_rate` decimal(10, 4) NULL DEFAULT NULL COMMENT '持有涨幅',
  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '记录更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_stock_code`(`stock_code` ASC) USING BTREE,
  INDEX `idx_entry_date`(`entry_date` ASC) USING BTREE,
  INDEX `idx_platform_dates`(`platform_start_date` ASC, `platform_end_date` ASC) USING BTREE,
  INDEX `idx_exit_date`(`exit_date` ASC) USING BTREE,
  INDEX `idx_stock_date`(`stock_code` ASC, `entry_date` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1661 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '持有涨幅记录表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
