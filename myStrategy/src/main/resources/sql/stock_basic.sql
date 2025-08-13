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

 Date: 14/08/2025 05:25:11
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for stock_basic
-- ----------------------------
DROP TABLE IF EXISTS `stock_basic`;
CREATE TABLE `stock_basic`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `stock_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '股票代码',
  `stock_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '股票名称',
  `market_type` tinyint(1) NULL DEFAULT NULL COMMENT '市场类型(0:深市,1:沪市,2:北交所,3科创)',
  `industry` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '所属行业',
  `industry_id` bigint NULL DEFAULT NULL,
  `company_id` bigint NULL DEFAULT NULL,
  `listing_date` date NULL DEFAULT NULL COMMENT '上市日期',
  `total_shares` decimal(20, 2) NULL DEFAULT NULL COMMENT '总股本(万股)',
  `circulating_shares` decimal(20, 2) NULL DEFAULT NULL COMMENT '流通股本(万股)',
  `is_st` tinyint(1) NULL DEFAULT 0 COMMENT '是否ST(0:否,1:是)',
  `status` tinyint(1) NULL DEFAULT 1 COMMENT '状态(0:退市,1:正常)',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_stock_code`(`stock_code` ASC) USING BTREE,
  INDEX `idx_industry`(`industry` ASC) USING BTREE,
  INDEX `idx_market`(`market_type` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 179413 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '股票基本信息表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
