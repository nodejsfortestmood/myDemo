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

 Date: 14/08/2025 05:26:09
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for stock_limit_up
-- ----------------------------
DROP TABLE IF EXISTS `stock_limit_up`;
CREATE TABLE `stock_limit_up`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `stock_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '股票代码',
  `trade_date` date NOT NULL COMMENT '交易日期',
  `first_limit_time` time NULL DEFAULT NULL COMMENT '首次涨停时间',
  `last_limit_time` time NULL DEFAULT NULL COMMENT '最后封板时间',
  `open_times` int NULL DEFAULT 0 COMMENT '开板次数',
  `limit_up_type` tinyint(1) NULL DEFAULT NULL COMMENT '涨停类型(1:首板,2:连板)',
  `consecutive_days` int NULL DEFAULT 1 COMMENT '连续涨停天数',
  `strength` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '强度描述',
  `main_net_inflow` decimal(20, 2) NULL DEFAULT NULL COMMENT '主力净流入(元)',
  `reason_types` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '异动原因类型(逗号分隔)',
  `reasons` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '异动原因(分号分隔)',
  `detail_reason` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '详细异动原因',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_stock_date`(`stock_code` ASC, `trade_date` ASC) USING BTREE,
  INDEX `idx_trade_date`(`trade_date` ASC) USING BTREE,
  INDEX `idx_limit_type`(`limit_up_type` ASC, `trade_date` ASC) USING BTREE,
  INDEX `idx_consecutive_days`(`consecutive_days` ASC, `trade_date` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '涨停板数据表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
