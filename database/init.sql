SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 创建数据库
CREATE DATABASE IF NOT EXISTS `scaffolding_db` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `scaffolding_db`;

-- 文件信息表
DROP TABLE IF EXISTS `file_info`;
CREATE TABLE `file_info` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `file_name` varchar(255) NOT NULL COMMENT '文件名称',
  `original_name` varchar(255) NOT NULL COMMENT '原始文件名',
  `file_path` varchar(500) NOT NULL COMMENT '文件路径',
  `file_size` bigint(20) DEFAULT '0' COMMENT '文件大小（字节）',
  `file_type` varchar(50) DEFAULT NULL COMMENT '文件类型',
  `file_extension` varchar(20) DEFAULT NULL COMMENT '文件扩展名',
  `upload_user_id` bigint(20) DEFAULT NULL COMMENT '上传人ID',
  `upload_user_name` varchar(50) DEFAULT NULL COMMENT '上传人姓名',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_file_type` (`file_type`),
  KEY `idx_upload_user_id` (`upload_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文件信息表';

-- 工作管理表
DROP TABLE IF EXISTS `work`;
CREATE TABLE `work` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `work_name` varchar(100) NOT NULL COMMENT '工作名称',
  `work_content` text COMMENT '工作内容',
  `work_status` varchar(20) DEFAULT 'pending' COMMENT '工作状态（pending-待处理，in_progress-进行中，completed-已完成，cancelled-已取消）',
  `work_time` datetime DEFAULT NULL COMMENT '工作时间',
  `start_time` datetime DEFAULT NULL COMMENT '开始时间',
  `end_time` datetime DEFAULT NULL COMMENT '结束时间',
  `priority` varchar(20) DEFAULT 'normal' COMMENT '优先级（low-低，normal-普通，high-高，urgent-紧急）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_work_status` (`work_status`),
  KEY `idx_work_time` (`work_time`),
  KEY `idx_priority` (`priority`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工作管理表';

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `username` varchar(50) NOT NULL COMMENT '用户名（账号）',
  `password` varchar(100) NOT NULL COMMENT '密码（不加密）',
  `nickname` varchar(50) DEFAULT NULL COMMENT '昵称',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`),
  KEY `idx_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 插入默认admin账号
INSERT INTO `user` (`username`, `password`, `nickname`) VALUES ('admin', '123456', '管理员');

-- 园区表
DROP TABLE IF EXISTS `park`;
CREATE TABLE `park` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `park_name` varchar(100) NOT NULL COMMENT '园区名称',
  `park_address` varchar(500) DEFAULT NULL COMMENT '园区地址',
  `park_contact` varchar(50) DEFAULT NULL COMMENT '园区联系人',
  `park_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_park_name` (`park_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='园区表';

-- 班组表
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `team_name` varchar(100) NOT NULL COMMENT '班组名称',
  `park_id` bigint(20) NOT NULL COMMENT '所属园区ID',
  `team_leader` varchar(50) DEFAULT NULL COMMENT '班组长',
  `leader_phone` varchar(20) DEFAULT NULL COMMENT '班组长电话',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_park_id` (`park_id`),
  KEY `idx_team_name` (`team_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班组表';

-- 工人表
DROP TABLE IF EXISTS `worker`;
CREATE TABLE `worker` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `worker_name` varchar(50) NOT NULL COMMENT '工人姓名',
  `worker_gender` varchar(10) NOT NULL COMMENT '性别（male-男，female-女）',
  `worker_phone` varchar(20) NOT NULL COMMENT '联系电话',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `hometown` varchar(200) DEFAULT NULL COMMENT '籍贯/户籍地',
  `is_out_of_town` tinyint(1) DEFAULT '1' COMMENT '是否外地（0-本地，1-外地）',
  `entry_status` varchar(20) DEFAULT 'pending' COMMENT '入职状态（pending-待确认，confirmed-已确认，rejected-已拒绝）',
  `team_id` bigint(20) DEFAULT NULL COMMENT '所属班组ID',
  `park_id` bigint(20) DEFAULT NULL COMMENT '所属园区ID（冗余，便于查询）',
  `night_shift_count` int(11) DEFAULT '0' COMMENT '每周夜班次数',
  `checkin_status` varchar(20) DEFAULT 'pending' COMMENT '入住状态（pending-待确认，confirmed-已确认，cancelled-已取消）',
  `checkin_confirm_time` datetime DEFAULT NULL COMMENT '入住确认时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_team_id` (`team_id`),
  KEY `idx_park_id` (`park_id`),
  KEY `idx_worker_name` (`worker_name`),
  KEY `idx_entry_status` (`entry_status`),
  KEY `idx_checkin_status` (`checkin_status`),
  KEY `idx_gender` (`worker_gender`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工人表';

-- 宿舍楼表
DROP TABLE IF EXISTS `dorm_building`;
CREATE TABLE `dorm_building` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `building_name` varchar(100) NOT NULL COMMENT '宿舍楼名称',
  `park_id` bigint(20) NOT NULL COMMENT '所属园区ID',
  `building_gender` varchar(10) DEFAULT 'mixed' COMMENT '楼栋性别限制（male-男，female-女，mixed-男女混合）',
  `total_floors` int(11) DEFAULT '6' COMMENT '总楼层数',
  `manager_name` varchar(50) DEFAULT NULL COMMENT '宿管姓名',
  `manager_phone` varchar(20) DEFAULT NULL COMMENT '宿管电话',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_park_id` (`park_id`),
  KEY `idx_building_gender` (`building_gender`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍楼表';

-- 宿舍房间表
DROP TABLE IF EXISTS `dorm_room`;
CREATE TABLE `dorm_room` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `room_number` varchar(20) NOT NULL COMMENT '房间号',
  `building_id` bigint(20) NOT NULL COMMENT '所属宿舍楼ID',
  `park_id` bigint(20) NOT NULL COMMENT '所属园区ID（冗余）',
  `floor` int(11) NOT NULL COMMENT '楼层',
  `room_gender` varchar(10) DEFAULT 'mixed' COMMENT '房间性别限制（male-男，female-女，mixed-混合）',
  `bed_count` int(11) DEFAULT '4' COMMENT '床位总数',
  `occupied_count` int(11) DEFAULT '0' COMMENT '已占用床位数',
  `is_night_shift_room` tinyint(1) DEFAULT '0' COMMENT '是否夜班专用房（0-否，1-是）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_building_room` (`building_id`, `room_number`),
  KEY `idx_building_id` (`building_id`),
  KEY `idx_park_id` (`park_id`),
  KEY `idx_room_gender` (`room_gender`),
  KEY `idx_night_shift` (`is_night_shift_room`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍房间表';

-- 床位表
DROP TABLE IF EXISTS `dorm_bed`;
CREATE TABLE `dorm_bed` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `bed_number` varchar(20) NOT NULL COMMENT '床位号（如：1号床/上铺）',
  `room_id` bigint(20) NOT NULL COMMENT '所属房间ID',
  `building_id` bigint(20) NOT NULL COMMENT '所属宿舍楼ID（冗余）',
  `park_id` bigint(20) NOT NULL COMMENT '所属园区ID（冗余）',
  `bed_status` varchar(20) DEFAULT 'available' COMMENT '床位状态（available-空闲，occupied-已占用，reserved-预留，maintenance-维修中）',
  `worker_id` bigint(20) DEFAULT NULL COMMENT '入住工人ID',
  `assign_time` datetime DEFAULT NULL COMMENT '分配时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_room_bed` (`room_id`, `bed_number`),
  KEY `idx_room_id` (`room_id`),
  KEY `idx_worker_id` (`worker_id`),
  KEY `idx_bed_status` (`bed_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='床位表';

-- 班车线路表
DROP TABLE IF EXISTS `shuttle_route`;
CREATE TABLE `shuttle_route` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `route_name` varchar(100) NOT NULL COMMENT '线路名称',
  `route_code` varchar(50) NOT NULL COMMENT '线路编号',
  `park_id` bigint(20) NOT NULL COMMENT '服务园区ID',
  `route_type` varchar(20) NOT NULL COMMENT '线路类型（morning-上班，evening-下班，both-双向）',
  `start_point` varchar(200) NOT NULL COMMENT '起点',
  `end_point` varchar(200) NOT NULL COMMENT '终点',
  `departure_time` varchar(20) DEFAULT NULL COMMENT '发车时间（HH:mm）',
  `return_time` varchar(20) DEFAULT NULL COMMENT '返程时间（HH:mm）',
  `capacity` int(11) DEFAULT '45' COMMENT '额定载客量',
  `driver_name` varchar(50) DEFAULT NULL COMMENT '司机姓名',
  `driver_phone` varchar(20) DEFAULT NULL COMMENT '司机电话',
  `plate_number` varchar(20) DEFAULT NULL COMMENT '车牌号',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_route_code` (`route_code`),
  KEY `idx_park_id` (`park_id`),
  KEY `idx_route_type` (`route_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班车线路表';

-- 班车站点表
DROP TABLE IF EXISTS `shuttle_stop`;
CREATE TABLE `shuttle_stop` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `route_id` bigint(20) NOT NULL COMMENT '所属线路ID',
  `stop_name` varchar(100) NOT NULL COMMENT '站点名称',
  `stop_order` int(11) NOT NULL COMMENT '站点顺序',
  `arrival_time` varchar(20) DEFAULT NULL COMMENT '预计到达时间',
  `stop_address` varchar(500) DEFAULT NULL COMMENT '站点地址',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_route_id` (`route_id`),
  KEY `idx_stop_order` (`stop_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='班车站点表';

-- 工人班车匹配表
DROP TABLE IF EXISTS `worker_shuttle`;
CREATE TABLE `worker_shuttle` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `worker_id` bigint(20) NOT NULL COMMENT '工人ID',
  `park_id` bigint(20) NOT NULL COMMENT '园区ID',
  `morning_route_id` bigint(20) DEFAULT NULL COMMENT '上班班车线路ID',
  `morning_stop_id` bigint(20) DEFAULT NULL COMMENT '上班上车站点ID',
  `evening_route_id` bigint(20) DEFAULT NULL COMMENT '下班班车线路ID',
  `evening_stop_id` bigint(20) DEFAULT NULL COMMENT '下班下车站点ID',
  `assign_time` datetime DEFAULT NULL COMMENT '分配时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_worker_id` (`worker_id`),
  KEY `idx_worker_id` (`worker_id`),
  KEY `idx_park_id` (`park_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='工人班车匹配表';

-- 到岗提醒表
DROP TABLE IF EXISTS `duty_reminder`;
CREATE TABLE `duty_reminder` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `worker_id` bigint(20) NOT NULL COMMENT '工人ID',
  `park_id` bigint(20) NOT NULL COMMENT '园区ID',
  `reminder_type` varchar(20) NOT NULL COMMENT '提醒类型（morning-上班，evening-下班，night-夜班）',
  `reminder_time` varchar(20) NOT NULL COMMENT '提醒时间（HH:mm）',
  `reminder_content` varchar(500) DEFAULT NULL COMMENT '提醒内容',
  `is_enabled` tinyint(1) DEFAULT '1' COMMENT '是否启用（0-否，1-是）',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  KEY `idx_worker_id` (`worker_id`),
  KEY `idx_park_id` (`park_id`),
  KEY `idx_reminder_type` (`reminder_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='到岗提醒表';

-- 园区调动记录表
DROP TABLE IF EXISTS `park_transfer_log`;
CREATE TABLE `park_transfer_log` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `transfer_no` varchar(50) NOT NULL COMMENT '调动单号',
  `transfer_type` varchar(20) DEFAULT 'team' COMMENT '调动类型（team-班组调动，worker-个人调动）',
  `team_id` bigint(20) DEFAULT NULL COMMENT '班组ID（班组调动时）',
  `worker_id` bigint(20) DEFAULT NULL COMMENT '工人ID（个人调动时）',
  `from_park_id` bigint(20) NOT NULL COMMENT '原园区ID',
  `to_park_id` bigint(20) NOT NULL COMMENT '新园区ID',
  `from_team_id` bigint(20) DEFAULT NULL COMMENT '原班组ID',
  `to_team_id` bigint(20) DEFAULT NULL COMMENT '新班组ID',
  `old_bed_id` bigint(20) DEFAULT NULL COMMENT '原床位ID',
  `new_bed_id` bigint(20) DEFAULT NULL COMMENT '新床位ID',
  `old_morning_route_id` bigint(20) DEFAULT NULL COMMENT '原上班线路ID',
  `new_morning_route_id` bigint(20) DEFAULT NULL COMMENT '新上班线路ID',
  `old_evening_route_id` bigint(20) DEFAULT NULL COMMENT '原下班线路ID',
  `new_evening_route_id` bigint(20) DEFAULT NULL COMMENT '新下班线路ID',
  `transfer_reason` varchar(500) DEFAULT NULL COMMENT '调动原因',
  `operator_id` bigint(20) DEFAULT NULL COMMENT '操作人ID',
  `operator_name` varchar(50) DEFAULT NULL COMMENT '操作人姓名',
  `transfer_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '调动时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '逻辑删除标识（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_transfer_no` (`transfer_no`),
  KEY `idx_team_id` (`team_id`),
  KEY `idx_worker_id` (`worker_id`),
  KEY `idx_from_park` (`from_park_id`),
  KEY `idx_to_park` (`to_park_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='园区调动记录表';

-- 初始化园区数据
INSERT INTO `park` (`park_name`, `park_address`, `park_contact`, `park_phone`) VALUES
('东部产业园', '城东开发区科技路1号', '张主任', '13800000001'),
('西部产业园', '城西工业区创业大道88号', '李主任', '13800000002'),
('南部产业园', '南郊工业园创新路66号', '王主任', '13800000003');

-- 初始化班组数据
INSERT INTO `team` (`team_name`, `park_id`, `team_leader`, `leader_phone`) VALUES
('装配一班', 1, '陈刚', '13900000001'),
('装配二班', 1, '刘强', '13900000002'),
('焊接一班', 2, '赵伟', '13900000003'),
('涂装一班', 3, '孙明', '13900000004');

-- 初始化宿舍楼数据
INSERT INTO `dorm_building` (`building_name`, `park_id`, `building_gender`, `total_floors`, `manager_name`, `manager_phone`) VALUES
('东园1号楼（男）', 1, 'male', 6, '王阿姨', '13700000001'),
('东园2号楼（女）', 1, 'female', 6, '李阿姨', '13700000002'),
('东园3号楼（夜班）', 1, 'mixed', 6, '张阿姨', '13700000003'),
('西园1号楼（男）', 2, 'male', 6, '赵阿姨', '13700000004'),
('西园2号楼（女）', 2, 'female', 6, '孙阿姨', '13700000005'),
('南园1号楼', 3, 'mixed', 6, '周阿姨', '13700000006');

-- 初始化宿舍房间数据
INSERT INTO `dorm_room` (`room_number`, `building_id`, `park_id`, `floor`, `room_gender`, `bed_count`, `is_night_shift_room`) VALUES
('101', 1, 1, 1, 'male', 4, 0), ('102', 1, 1, 1, 'male', 4, 0), ('103', 1, 1, 1, 'male', 4, 0),
('201', 1, 1, 2, 'male', 4, 0), ('202', 1, 1, 2, 'male', 4, 0),
('101', 2, 1, 1, 'female', 4, 0), ('102', 2, 1, 1, 'female', 4, 0),
('101', 3, 1, 1, 'male', 4, 1), ('102', 3, 1, 1, 'female', 4, 1),
('101', 4, 2, 1, 'male', 4, 0), ('102', 4, 2, 1, 'male', 4, 0),
('101', 5, 2, 1, 'female', 4, 0),
('101', 6, 3, 1, 'mixed', 4, 0), ('102', 6, 3, 1, 'mixed', 4, 0);

-- 初始化床位数据
INSERT INTO `dorm_bed` (`bed_number`, `room_id`, `building_id`, `park_id`) VALUES
('1号床', 1, 1, 1), ('2号床', 1, 1, 1), ('3号床', 1, 1, 1), ('4号床', 1, 1, 1),
('1号床', 2, 1, 1), ('2号床', 2, 1, 1), ('3号床', 2, 1, 1), ('4号床', 2, 1, 1),
('1号床', 3, 1, 1), ('2号床', 3, 1, 1), ('3号床', 3, 1, 1), ('4号床', 3, 1, 1),
('1号床', 4, 1, 1), ('2号床', 4, 1, 1), ('3号床', 4, 1, 1), ('4号床', 4, 1, 1),
('1号床', 5, 1, 1), ('2号床', 5, 1, 1), ('3号床', 5, 1, 1), ('4号床', 5, 1, 1),
('1号床', 6, 2, 1), ('2号床', 6, 2, 1), ('3号床', 6, 2, 1), ('4号床', 6, 2, 1),
('1号床', 7, 2, 1), ('2号床', 7, 2, 1), ('3号床', 7, 2, 1), ('4号床', 7, 2, 1),
('1号床', 8, 3, 1), ('2号床', 8, 3, 1), ('3号床', 8, 3, 1), ('4号床', 8, 3, 1),
('1号床', 9, 3, 1), ('2号床', 9, 3, 1), ('3号床', 9, 3, 1), ('4号床', 9, 3, 1),
('1号床', 10, 4, 2), ('2号床', 10, 4, 2), ('3号床', 10, 4, 2), ('4号床', 10, 4, 2),
('1号床', 11, 4, 2), ('2号床', 11, 4, 2), ('3号床', 11, 4, 2), ('4号床', 11, 4, 2),
('1号床', 12, 5, 2), ('2号床', 12, 5, 2), ('3号床', 12, 5, 2), ('4号床', 12, 5, 2),
('1号床', 13, 6, 3), ('2号床', 13, 6, 3), ('3号床', 13, 6, 3), ('4号床', 13, 6, 3),
('1号床', 14, 6, 3), ('2号床', 14, 6, 3), ('3号床', 14, 6, 3), ('4号床', 14, 6, 3);

-- 初始化班车线路数据
INSERT INTO `shuttle_route` (`route_name`, `route_code`, `park_id`, `route_type`, `start_point`, `end_point`, `departure_time`, `return_time`, `driver_name`, `driver_phone`, `plate_number`) VALUES
('东园早班1号线', 'DY-M-001', 1, 'morning', '东苑宿舍区', '东部产业园大门', '07:30', NULL, '张师傅', '13600000001', '沪A12345'),
('东园晚班1号线', 'DY-E-001', 1, 'evening', '东部产业园大门', '东苑宿舍区', NULL, '18:00', '张师傅', '13600000001', '沪A12345'),
('东园夜班1号线', 'DY-N-001', 1, 'both', '东苑宿舍区', '东部产业园大门', '19:30', '07:00', '李师傅', '13600000002', '沪A12346'),
('西园早班1号线', 'XY-M-001', 2, 'morning', '西苑宿舍区', '西部产业园大门', '07:40', NULL, '王师傅', '13600000003', '沪A12347'),
('西园晚班1号线', 'XY-E-001', 2, 'evening', '西部产业园大门', '西苑宿舍区', NULL, '18:10', '王师傅', '13600000003', '沪A12347'),
('南园早班1号线', 'NY-M-001', 3, 'morning', '南苑宿舍区', '南部产业园大门', '07:20', NULL, '赵师傅', '13600000004', '沪A12348'),
('南园晚班1号线', 'NY-E-001', 3, 'evening', '南部产业园大门', '南苑宿舍区', NULL, '17:50', '赵师傅', '13600000004', '沪A12348');

-- 初始化班车站点数据
INSERT INTO `shuttle_stop` (`route_id`, `stop_name`, `stop_order`, `arrival_time`) VALUES
(1, '东苑宿舍区正门', 1, '07:30'), (1, '东苑1号楼', 2, '07:32'), (1, '东苑3号楼（夜班楼）', 3, '07:35'), (1, '东部产业园大门', 4, '07:50'),
(2, '东部产业园大门', 1, '18:00'), (2, '东苑3号楼（夜班楼）', 2, '18:15'), (2, '东苑1号楼', 3, '18:18'), (2, '东苑宿舍区正门', 4, '18:20'),
(3, '东苑宿舍区正门', 1, '19:30'), (3, '东苑3号楼（夜班楼）', 2, '19:33'), (3, '东部产业园大门', 3, '19:48'),
(4, '西苑宿舍区正门', 1, '07:40'), (4, '西苑1号楼', 2, '07:42'), (4, '西部产业园大门', 3, '08:00'),
(5, '西部产业园大门', 1, '18:10'), (5, '西苑1号楼', 2, '18:28'), (5, '西苑宿舍区正门', 3, '18:30'),
(6, '南苑宿舍区正门', 1, '07:20'), (6, '南园1号楼', 2, '07:22'), (6, '南部产业园大门', 3, '07:45'),
(7, '南部产业园大门', 1, '17:50'), (7, '南园1号楼', 2, '18:13'), (7, '南苑宿舍区正门', 3, '18:15');

-- 初始化测试工人数据
INSERT INTO `worker` (`worker_name`, `worker_gender`, `worker_phone`, `id_card`, `hometown`, `is_out_of_town`, `entry_status`, `team_id`, `park_id`, `night_shift_count`, `checkin_status`) VALUES
('张伟', 'male', '13810000001', '320101199001010001', '江苏省南京市', 1, 'confirmed', 1, 1, 2, 'pending'),
('李娜', 'female', '13810000002', '320101199202020002', '浙江省杭州市', 1, 'confirmed', 1, 1, 0, 'confirmed'),
('王强', 'male', '13810000003', '320101198803030003', '安徽省合肥市', 1, 'confirmed', 1, 1, 4, 'pending'),
('赵敏', 'female', '13810000004', '320101199404040004', '江西省南昌市', 1, 'confirmed', 2, 1, 0, 'pending'),
('孙浩', 'male', '13810000005', '320101199105050005', '山东省济南市', 1, 'pending', NULL, NULL, 0, 'pending'),
('周磊', 'male', '13810000006', '320101198906060006', '河南省郑州市', 1, 'confirmed', 3, 2, 1, 'confirmed'),
('吴芳', 'female', '13810000007', '320101199307070007', '湖南省长沙市', 1, 'confirmed', 4, 3, 0, 'pending');

SET FOREIGN_KEY_CHECKS = 1;
