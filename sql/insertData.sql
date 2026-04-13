USE `quick_start`;

-- 菜单测试数据
INSERT INTO `qs_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `menu_type`, `visible`, `perms`, `icon`, `status`)
VALUES
    (1001, '系统管理', 0, 1, '/system', 'Layout', 1, 1, 0, NULL, 'system', 1),
    (1002, '用户管理', 1001, 1, '/system/user', 'system/user/index', 1, 2, 0, 'system:user:list', 'user', 1),
    (1003, '查询用户', 1002, 1, '', '', 1, 3, 0, 'system:user:query', '#', 1),
    (1004, '新增用户', 1002, 2, '', '', 1, 3, 0, 'system:user:add', '#', 1),
    (1005, '修改用户', 1002, 3, '', '', 1, 3, 0, 'system:user:edit', '#', 1),
    (1101, '菜单管理', 1001, 2, '/system/menu', 'system/menu/index', 1, 2, 0, 'system:menu:list', 'tree', 1),
    (1102, '查询菜单', 1101, 1, '', '', 1, 3, 0, 'system:menu:query', '#', 1),
    (1103, '新增菜单', 1101, 2, '', '', 1, 3, 0, 'system:menu:add', '#', 1),
    (1104, '修改菜单', 1101, 3, '', '', 1, 3, 0, 'system:menu:edit', '#', 1),
    (1201, '角色管理', 1001, 3, '/system/role', 'system/role/index', 1, 2, 0, 'system:role:list', 'peoples', 1),
    (1202, '查询角色', 1201, 1, '', '', 1, 3, 0, 'system:role:query', '#', 1),
    (1203, '新增角色', 1201, 2, '', '', 1, 3, 0, 'system:role:add', '#', 1),
    (1204, '修改角色', 1201, 3, '', '', 1, 3, 0, 'system:role:edit', '#', 1);

-- 角色测试数据
INSERT INTO `qs_role` (`role_id`, `role_name`, `role_key`, `role_sort`, `remark`, `status`, `deleted_flag`)
VALUES
    (1, '超级管理员', 'admin', 1, '拥有所有菜单和权限', 1, 0),
    (2, '运营人员', 'operator', 2, '只能查看用户、菜单、角色', 1, 0);

-- 用户测试数据
INSERT INTO `qs_user` (`user_id`, `user_code`, `user_type`, `user_name`, `phone`, `email`, `password`, `gender`, `register_source`, `login_count`, `member_level_score`, `credit_score`, `status`, `deleted_flag`)
VALUES
    (1, 'QS000001', 1, '超级管理员', '13800000001', 'admin@quickstart.com', '$2a$10$oHh0XO3Y2PRt8rKbG/4yne7MYDgHVU9uWTw/3R7mGbIRYM7dhavvK', 1, 3, 0, 0.00, 100, 1, 0),
    (2, 'QS000002', 1, '运营主管', '13800000002', 'operator@quickstart.com', '$2a$10$0jVJHp.m6pq0ekIlZrBUsO8WnoifkC7JxtnNkwkq83JZTU1e2B4B2', 1, 3, 0, 0.00, 90, 1, 0),
    (3, 'QS000003', 2, '普通用户', '13800000003', 'client@quickstart.com', '$2a$10$0jVJHp.m6pq0ekIlZrBUsO8WnoifkC7JxtnNkwkq83JZTU1e2B4B2', 2, 1, 0, 0.00, 80, 1, 0);

-- 角色菜单关联
INSERT INTO `qs_role_menu` (`role_menu_id`, `role_id`, `menu_id`)
VALUES
    (1, 1, 1001),
    (2, 1, 1002),
    (3, 1, 1003),
    (4, 1, 1004),
    (5, 1, 1005),
    (6, 1, 1101),
    (7, 1, 1102),
    (8, 1, 1103),
    (9, 1, 1104),
    (10, 1, 1201),
    (11, 1, 1202),
    (12, 1, 1203),
    (13, 1, 1204),
    (14, 2, 1001),
    (15, 2, 1002),
    (16, 2, 1003),
    (17, 2, 1101),
    (18, 2, 1102),
    (19, 2, 1201),
    (20, 2, 1202);

-- 用户角色关联
INSERT INTO `qs_user_role` (`user_role_id`, `user_id`, `role_id`)
VALUES
    (1, 1, 1),
    (2, 2, 2);

-- 登录测试账号
-- 超级管理员: 13800000001 / admin123
-- 运营主管:   13800000002 / 123456
-- 客户端用户: 13800000003 / 123456
