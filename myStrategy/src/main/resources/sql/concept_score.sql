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

 Date: 14/08/2025 05:25:00
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for concept_score
-- ----------------------------
DROP TABLE IF EXISTS `concept_score`;
CREATE TABLE `concept_score`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `concept_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '概念名称',
  `trade_date` date NOT NULL COMMENT '交易日期',
  `avg_rise` decimal(10, 4) NOT NULL COMMENT '平均涨幅(%)',
  `limit_ups` int NOT NULL DEFAULT 0 COMMENT '涨停股数量',
  `big_vols` int NOT NULL DEFAULT 0 COMMENT '放量股数量',
  `turnover_rate` decimal(10, 4) NOT NULL COMMENT '平均换手率(%)',
  `stock_size` int NOT NULL COMMENT '成分股数量',
  `score` decimal(10, 4) NOT NULL COMMENT '概念热度得分',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_concept_date`(`concept_name` ASC, `trade_date` ASC) USING BTREE COMMENT '概念+日期唯一索引',
  INDEX `idx_trade_date`(`trade_date` ASC) USING BTREE COMMENT '交易日期索引',
  INDEX `idx_score`(`score` ASC) USING BTREE COMMENT '得分索引'
) ENGINE = InnoDB AUTO_INCREMENT = 12186 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '概念热度评分表' ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
