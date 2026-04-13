

-- === 说明 ===
# 主键类型都为bigint,并且主键名称不能为id
# 每个表都有一下三个字段，部分表只有create_time字段表示其不可修改
# 含有status字段代表需要添加的时候需要审核
#     `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态：0-待审核 1-启用 2-下架 5-禁用',
#     `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
#     `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
#     `deleted_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除状态：0-未删除 1-已删除',




DROP DATABASE IF EXISTS `quick_start`;
CREATE DATABASE IF NOT EXISTS `quick_start` ;
USE `quick_start`;




-- 一、系统相关表
-- 1.1 菜单权限表
DROP TABLE IF EXISTS `qs_menu`;
CREATE TABLE `qs_menu`
(
    `menu_id` int NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    `menu_name` varchar(50) NOT NULL COMMENT '菜单名称',
    `parent_id` int DEFAULT 0 COMMENT '父菜单ID',
    `order_num` tinyint(4) DEFAULT 0 COMMENT '显示顺序',
    `path` varchar(200) DEFAULT '' COMMENT '路由地址',
    `component` varchar(255) DEFAULT NULL COMMENT '组件路径',
    `is_frame` tinyint(1) DEFAULT 1 COMMENT '是否为外链（0是 1否）',
    `menu_type` tinyint(1) DEFAULT 1 COMMENT '菜单类型（1目录 2菜单 3按钮）',
    `visible` tinyint(1) DEFAULT 0 COMMENT '菜单状态（0显示 1隐藏）',
    `perms` varchar(100) DEFAULT NULL COMMENT '权限标识（如content:article:list）',
    `icon` varchar(100) DEFAULT '#' COMMENT '菜单图标',

    `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态：1-启用 2-下架 ',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2034 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜单权限表';



-- 1.2 角色表
DROP TABLE IF EXISTS `qs_role`;
CREATE TABLE `qs_role`
(
    `role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_name` varchar(30) NOT NULL COMMENT '角色名称',
    `role_key` varchar(100) NOT NULL COMMENT '角色权限字符串',
    `role_sort` int(4) NOT NULL COMMENT '显示顺序',

    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态：1-启用 2-下架 ',
    `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除状态：0-未删除 1-已删除',
    PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色信息表';




-- 1.3 角色菜单关联表
DROP TABLE IF EXISTS `qs_role_menu`;
CREATE TABLE `qs_role_menu`
(
    `role_menu_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `menu_id` bigint NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (`role_menu_id`),
    UNIQUE KEY `uk_role_menu` (`role_id`,`menu_id`) COMMENT '确保角色与菜单的关联唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色和菜单关联表';



-- 1.4 用户角色关联表
DROP TABLE IF EXISTS `qs_user_role`;
CREATE TABLE `qs_user_role`
(
    `user_role_id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    PRIMARY KEY (`user_role_id`),
    UNIQUE KEY `uk_user_role` (`user_id`,`role_id`) COMMENT '确保用户与角色的关联唯一'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户和角色关联表';




-- 二，业务相关表
-- qs_user
DROP TABLE IF EXISTS `qs_user`;
CREATE TABLE `qs_user`
(
    `user_id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `user_code` varchar(50)  NOT NULL COMMENT '用户编码',
    `user_type` int(2)  NOT NULL COMMENT '用户类型',
    `user_name` varchar(50)  NOT NULL COMMENT '用户昵称',
    `phone` varchar(20)  NOT NULL COMMENT '手机号',
    `email` varchar(100)  NULL DEFAULT NULL COMMENT '邮箱',
    `password` varchar(100)  NOT NULL COMMENT '密码（加密）',
    `avatar` varchar(500)  NULL DEFAULT NULL COMMENT '头像',
    `gender` tinyint(1) NULL DEFAULT 0 COMMENT '性别：0-未知 1-男 2-女',
    `birthday` datetime NULL DEFAULT NULL COMMENT '生日',
    `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` varchar(50) NULL DEFAULT NULL COMMENT '最后登录IP',
    `register_source` int(2) NOT NULL DEFAULT 1 COMMENT '注册来源：1-小程序 2-APP 3-H5',
    `openid` varchar(100)  NULL DEFAULT NULL COMMENT '微信openid',
    `unionid` varchar(100) NULL DEFAULT NULL COMMENT '微信unionid',
    `login_count` int(11) NULL DEFAULT 0 COMMENT '登录次数',
    `member_level_score` decimal(10,2) NULL DEFAULT 0.00 COMMENT '会员等级分',
    `credit_score` int(11) NULL DEFAULT 0 COMMENT '信誉分数',

    `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '用户状态：0-待审核 1-启用 2-下架 5-禁用',
    `deleted_flag` tinyint(1) NOT NULL DEFAULT 0 COMMENT '删除状态：0-未删除 1-已删除',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`user_id`),
    UNIQUE KEY `uk_user_code` (`user_code`),
    UNIQUE KEY `uk_phone` (`phone`),
    UNIQUE KEY `uk_openid` (`openid`),

    KEY `idx_deleted_status_createtime` (`deleted_flag`, `status`, `create_time`)
) ENGINE = InnoDB AUTO_INCREMENT = 36 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '用户表' ROW_FORMAT = Dynamic;



