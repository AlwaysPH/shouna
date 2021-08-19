/*
SQLyog Ultimate v8.53 
MySQL - 5.7.13 : Database - hpshop
*********************************************************************
*/

/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`hpshop` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `hpshop`;

/*Table structure for table `customer_address` */

DROP TABLE IF EXISTS `customer_address`;

CREATE TABLE `customer_address` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `realname` varchar(64) DEFAULT NULL COMMENT '姓名',
  `telphone` varchar(64) DEFAULT NULL COMMENT '手机号码',
  `address` varchar(128) DEFAULT NULL COMMENT '地址',
  `is_default` varchar(64) DEFAULT NULL COMMENT '是否默认',
  `customer_id` int(11) DEFAULT NULL COMMENT '客户id',
  `province` varchar(64) DEFAULT NULL COMMENT '省份',
  `city` varchar(64) DEFAULT NULL COMMENT '城市',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `customer_address` */

/*Table structure for table `customer_bank` */

DROP TABLE IF EXISTS `customer_bank`;

CREATE TABLE `customer_bank` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `customer_id` varchar(64) DEFAULT NULL COMMENT '客户id',
  `account_name` varchar(64) DEFAULT NULL COMMENT '户主姓名',
  `card_no` varchar(64) DEFAULT NULL COMMENT '卡号',
  `telphone` varchar(64) DEFAULT NULL COMMENT '电话号码',
  `bank_name` varchar(64) DEFAULT NULL COMMENT '银行名称',
  `bank_address` varchar(64) DEFAULT NULL COMMENT '银行地址',
  `is_default` varchar(64) DEFAULT NULL COMMENT '是否默认',
  `identity` varchar(64) DEFAULT NULL COMMENT '身份证号码',
  `branch` varchar(64) DEFAULT NULL COMMENT '银行分行编号',
  `account_type` int(11) DEFAULT NULL COMMENT '账户类型 1信用账户 2储蓄账户',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `customer_bank` */

/*Table structure for table `customer_bill` */

DROP TABLE IF EXISTS `customer_bill`;

CREATE TABLE `customer_bill` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `customer_id` int(11) DEFAULT NULL COMMENT '客户id',
  `type` varchar(64) DEFAULT NULL COMMENT '账单类型',
  `data_id` varchar(64) DEFAULT NULL COMMENT '订单id',
  `agent_level` int(11) DEFAULT NULL COMMENT '代理层级',
  `content` text COMMENT '交易说明',
  `amount` decimal(10,4) DEFAULT NULL COMMENT '交易金额',
  `balance` decimal(10,4) DEFAULT NULL COMMENT '余额',
  `addtime` datetime DEFAULT NULL COMMENT '添加时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `customer_bill` */

/*Table structure for table `customer_cashout` */

DROP TABLE IF EXISTS `customer_cashout`;

CREATE TABLE `customer_cashout` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `customer_id` int(11) DEFAULT NULL COMMENT '客户id',
  `account_name` varchar(64) DEFAULT NULL COMMENT '开户姓名',
  `bank_name` varchar(64) DEFAULT NULL COMMENT '银行名',
  `card_no` varchar(64) DEFAULT NULL COMMENT '卡号',
  `bank_address` varchar(128) DEFAULT NULL COMMENT '银行地址',
  `amount` decimal(10,4) DEFAULT NULL COMMENT '提现金额',
  `status` varchar(64) DEFAULT NULL COMMENT '状态',
  `remark` varchar(64) DEFAULT NULL COMMENT '备注',
  `user_id` int(11) DEFAULT NULL COMMENT '处理人id',
  `username` varchar(64) DEFAULT NULL COMMENT '处理人账号',
  `addtime` datetime DEFAULT NULL COMMENT '申请时间',
  `overtime` datetime DEFAULT NULL COMMENT '结束时间',
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单号',
  `pay_amount` decimal(10,4) DEFAULT NULL COMMENT '支付金额',
  `pay_no` varchar(64) DEFAULT NULL COMMENT '交易编号',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `customer_cashout` */

/*Table structure for table `customer_info` */

DROP TABLE IF EXISTS `customer_info`;

CREATE TABLE `customer_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `username` varchar(64) DEFAULT NULL COMMENT '用户名',
  `password` varchar(64) DEFAULT NULL COMMENT '密码',
  `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
  `openid` varchar(64) DEFAULT NULL COMMENT '微信openid',
  `type` int(11) DEFAULT NULL COMMENT '0客户 1商家',
  `headimg` varchar(64) DEFAULT NULL COMMENT '头像',
  `telphone` varchar(64) DEFAULT NULL COMMENT '手机号码',
  `invite_code` varchar(64) DEFAULT NULL COMMENT '邀请码',
  `pid` int(11) DEFAULT NULL COMMENT '上级id',
  `status` int(11) DEFAULT NULL COMMENT '状态0、正常 1、禁用',
  `level_id` int(11) DEFAULT NULL COMMENT '等级id',
  `level_num` int(11) DEFAULT NULL COMMENT '等级数',
  `agent_level` int(11) DEFAULT NULL COMMENT '第几级代理',
  `total_order` int(11) DEFAULT NULL COMMENT '总下单数',
  `total_money` decimal(10,4) DEFAULT NULL COMMENT '总下单金额',
  `balance` decimal(10,4) DEFAULT NULL COMMENT '余额',
  `version` int(11) DEFAULT NULL COMMENT '版本号',
  `regtime` datetime DEFAULT NULL COMMENT '注册时间',
  `regip` varchar(64) DEFAULT NULL COMMENT '注册ip',
  `logintime` datetime DEFAULT NULL COMMENT '登陆时间',
  `loginip` varchar(64) DEFAULT NULL COMMENT '登录ip',
  `agenttime` datetime DEFAULT NULL COMMENT '登陆时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `customer_info` */

/*Table structure for table `customer_joininfo` */

DROP TABLE IF EXISTS `customer_joininfo`;

CREATE TABLE `customer_joininfo` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `realname` varchar(64) DEFAULT NULL COMMENT '项目名称',
  `itemname` varchar(64) DEFAULT NULL COMMENT '加盟项目',
  `text` text COMMENT '加盟说明',
  `telephone` varchar(64) DEFAULT NULL COMMENT '联系号码',
  `recommend` varchar(64) DEFAULT NULL COMMENT '推荐人',
  `addtime` datetime DEFAULT NULL COMMENT '添加时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `customer_joininfo` */

/*Table structure for table `customer_level` */

DROP TABLE IF EXISTS `customer_level`;

CREATE TABLE `customer_level` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `level` bigint(11) DEFAULT NULL COMMENT '等级标识',
  `title` varchar(64) DEFAULT NULL COMMENT '等级名称',
  `title_en` varchar(64) DEFAULT NULL COMMENT '等级英文',
  `status` bigint(11) DEFAULT NULL COMMENT '状态 0正常 1禁用',
  `order_rate` decimal(4,4) DEFAULT NULL COMMENT '返佣率',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `customer_level` */

/*Table structure for table `customer_order` */

DROP TABLE IF EXISTS `customer_order`;

CREATE TABLE `customer_order` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `customer_id` bigint(11) DEFAULT NULL COMMENT '客户id',
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单号',
  `item_id` bigint(11) DEFAULT NULL COMMENT '项目id',
  `item_name` varchar(64) DEFAULT NULL COMMENT '项目名称',
  `price` decimal(10,4) DEFAULT NULL COMMENT '价格',
  `status` bigint(11) DEFAULT NULL COMMENT '状态 0待接单 1待上门 2服务中 3已完成 4已支付 5退款',
  `settle_status` bigint(11) DEFAULT NULL COMMENT '结算状态0待支付 1已支付',
  `commission` decimal(10,4) DEFAULT NULL COMMENT '佣金',
  `order_rate` decimal(4,4) DEFAULT NULL COMMENT '返点',
  `level_id` bigint(11) DEFAULT NULL COMMENT '级别id',
  `countdown` datetime DEFAULT NULL COMMENT '倒计时时间',
  `addtime` datetime DEFAULT NULL COMMENT '下单时间',
  `overtime` datetime DEFAULT NULL COMMENT '结束时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `customer_order` */

/*Table structure for table `customer_order_evaluate` */

DROP TABLE IF EXISTS `customer_order_evaluate`;

CREATE TABLE `customer_order_evaluate` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单号',
  `arranger_id` bigint(11) DEFAULT NULL COMMENT '整理师id',
  `realname` varchar(64) DEFAULT NULL COMMENT '姓名',
  `star` varchar(64) DEFAULT NULL COMMENT '1-5星',
  `point` varchar(32) DEFAULT NULL COMMENT '分数',
  `text` text COMMENT '说明',
  `addtime` datetime DEFAULT NULL COMMENT '添加时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `customer_order_evaluate` */

/*Table structure for table `customer_recharge` */

DROP TABLE IF EXISTS `customer_recharge`;

CREATE TABLE `customer_recharge` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `customer_id` bigint(11) DEFAULT NULL COMMENT '用户id',
  `channel_id` bigint(11) DEFAULT NULL COMMENT '支付渠道id',
  `level_id` bigint(11) DEFAULT NULL COMMENT '级别id',
  `order_no` varchar(64) DEFAULT NULL COMMENT '订单号',
  `pay_no` varchar(64) DEFAULT NULL COMMENT '支付流水号',
  `amount` decimal(10,4) DEFAULT NULL COMMENT '订单金额',
  `pay_amount` decimal(10,4) DEFAULT NULL COMMENT '支付金额',
  `status` varchar(32) DEFAULT NULL COMMENT '状态',
  `paytype` varchar(32) DEFAULT NULL COMMENT '支付类型',
  `payurl` varchar(64) DEFAULT NULL COMMENT '支付地址',
  `paydata` varchar(128) DEFAULT NULL COMMENT '支付数据',
  `addtime` datetime DEFAULT NULL COMMENT '下单时间',
  `paytime` datetime DEFAULT NULL COMMENT '支付时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `customer_recharge` */

/*Table structure for table `item_arranger` */

DROP TABLE IF EXISTS `item_arranger`;

CREATE TABLE `item_arranger` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `item_id` bigint(11) DEFAULT NULL COMMENT '项目id',
  `realname` varchar(64) DEFAULT NULL COMMENT '姓名',
  `code` varchar(64) DEFAULT NULL COMMENT '代码',
  `grade` varchar(32) DEFAULT NULL COMMENT '级别 初 中 高',
  `amount` decimal(10,4) DEFAULT NULL COMMENT '价格',
  `status` int(11) DEFAULT NULL COMMENT '0空闲 1服务中',
  `text` text COMMENT '说明',
  `addtime` datetime DEFAULT NULL COMMENT '添加时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `item_arranger` */

/*Table structure for table `item_category` */

DROP TABLE IF EXISTS `item_category`;

CREATE TABLE `item_category` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(64) DEFAULT NULL COMMENT '名称',
  `code` varchar(64) DEFAULT NULL COMMENT '代码',
  `text` text COMMENT '说明',
  `img` varchar(64) DEFAULT NULL COMMENT '图片地址',
  `sort` bigint(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `item_category` */

/*Table structure for table `item_comment` */

DROP TABLE IF EXISTS `item_comment`;

CREATE TABLE `item_comment` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `item_id` bigint(11) DEFAULT NULL COMMENT '项目id',
  `customer_id` bigint(11) DEFAULT NULL COMMENT '客户id',
  `order_id` bigint(11) DEFAULT NULL COMMENT '订单id',
  `lable` varchar(64) DEFAULT NULL COMMENT '标签',
  `comment` varchar(128) DEFAULT NULL COMMENT '评论内容',
  `img` varchar(64) DEFAULT NULL COMMENT '图片列表',
  `start` varchar(64) DEFAULT NULL COMMENT '评价星级',
  `score` bigint(11) DEFAULT NULL COMMENT '分数',
  `pid` bigint(11) DEFAULT NULL COMMENT '上级评论id',
  `addtime` datetime DEFAULT NULL COMMENT '添加时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `item_comment` */

/*Table structure for table `item_info` */

DROP TABLE IF EXISTS `item_info`;

CREATE TABLE `item_info` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(64) DEFAULT NULL COMMENT '项目名称',
  `code` varchar(64) DEFAULT NULL COMMENT '项目代码',
  `type` bigint(11) DEFAULT NULL COMMENT '0收纳 1课程',
  `text` text COMMENT '项目说明',
  `amount` decimal(10,4) DEFAULT NULL COMMENT '价格',
  `sale_amount` decimal(10,4) DEFAULT NULL COMMENT '活动价格',
  `imgurl` varchar(64) DEFAULT NULL COMMENT '图片地址',
  `time` datetime DEFAULT NULL COMMENT '时间',
  `addtime` datetime DEFAULT NULL COMMENT '添加时间',
  `overtime` datetime DEFAULT NULL COMMENT '结束时间',
  `sort` bigint(11) DEFAULT NULL COMMENT '排序',
  `category_id` bigint(11) DEFAULT NULL COMMENT '分类id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `item_info` */

/*Table structure for table `sys_article` */

DROP TABLE IF EXISTS `sys_article`;

CREATE TABLE `sys_article` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `title` varchar(32) DEFAULT NULL COMMENT '标题',
  `content` text COMMENT '内容',
  `author` varchar(64) DEFAULT NULL COMMENT '作者',
  `addtime` datetime DEFAULT NULL COMMENT '添加时间',
  `lasttime` datetime DEFAULT NULL COMMENT '修改时间',
  `img` varchar(128) DEFAULT NULL COMMENT '图片',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_article` */

/*Table structure for table `sys_config` */

DROP TABLE IF EXISTS `sys_config`;

CREATE TABLE `sys_config` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(64) DEFAULT NULL COMMENT '名称',
  `code` varchar(64) DEFAULT NULL COMMENT '编码',
  `content` text COMMENT '内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_config` */

/*Table structure for table `sys_message` */

DROP TABLE IF EXISTS `sys_message`;

CREATE TABLE `sys_message` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `type` varchar(32) DEFAULT NULL COMMENT '类型',
  `title` varchar(64) DEFAULT NULL COMMENT '标题',
  `content` text COMMENT '内容',
  `addtime` datetime DEFAULT NULL COMMENT '插入时间',
  `customer_id` bigint(11) DEFAULT NULL COMMENT '客户id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_message` */

/*Table structure for table `sys_opt_log` */

DROP TABLE IF EXISTS `sys_opt_log`;

CREATE TABLE `sys_opt_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `optType` varchar(32) DEFAULT NULL COMMENT '操作类型(login,operate)',
  `optContent` text COMMENT '操作内容',
  `ip` varchar(64) DEFAULT NULL COMMENT 'ip地址',
  `optTime` datetime DEFAULT NULL COMMENT '操作时间',
  `cusomer_id` bigint(11) DEFAULT NULL COMMENT '操作人id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/*Data for the table `sys_opt_log` */

/*Table structure for table `sys_resource` */

DROP TABLE IF EXISTS `sys_resource`;

CREATE TABLE `sys_resource` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(64) DEFAULT NULL COMMENT '名称',
  `icon` varchar(64) DEFAULT NULL COMMENT '图标',
  `code` varchar(128) DEFAULT NULL COMMENT '代码',
  `group_id` int(11) DEFAULT NULL COMMENT '分组id',
  `link_url` varchar(128) DEFAULT NULL COMMENT '跳转地址',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=14 DEFAULT CHARSET=utf8;

/*Data for the table `sys_resource` */

insert  into `sys_resource`(`id`,`name`,`icon`,`code`,`group_id`,`link_url`,`sort`) values (1,'产品列表',NULL,'item_list',1,'/admin/item',1),(2,'整理师',NULL,'item_arranger',1,'/admin/item/arranger',2),(3,'产品分类',NULL,'item_category',1,'/admin/item/categ',3),(4,'加盟商',NULL,'customer_joininfo',2,'/admin/customer/joininfo',1),(5,'客户信息',NULL,'customer_list',2,'/admin/customer/list',2),(6,'客户等级',NULL,'customer_level',2,'/admin/customer/level',3),(7,'客户订单',NULL,'customer_order',2,'/admin/customer/order',4),(8,'文章管理',NULL,'sys_article',3,'/admin/sys/article',1),(9,'配置中心',NULL,'sys_config',3,'/admin/sys/config',2),(10,'操作日志',NULL,'sys_optlog',3,'/admin/sys/optlog',3),(11,'系统资源',NULL,'sys_resource',3,'/admin/sys/resource',4),(12,'系统角色',NULL,'sys_role',3,'/admin/sys/role',5),(13,'系统用户',NULL,'sys_user',3,'/admin/sys/user',6);

/*Table structure for table `sys_resource_group` */

DROP TABLE IF EXISTS `sys_resource_group`;

CREATE TABLE `sys_resource_group` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `title` varchar(64) DEFAULT NULL COMMENT '标题',
  `icon` varchar(64) DEFAULT NULL COMMENT '图标',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;

/*Data for the table `sys_resource_group` */

insert  into `sys_resource_group`(`id`,`title`,`icon`,`sort`) values (1,'业务中心',NULL,1),(2,'客户管理',NULL,2),(3,'系统设置',NULL,3);

/*Table structure for table `sys_role` */

DROP TABLE IF EXISTS `sys_role`;

CREATE TABLE `sys_role` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `name` varchar(64) DEFAULT NULL COMMENT '名称',
  `code` varchar(64) DEFAULT NULL COMMENT '代码',
  `perms` text COMMENT '权限',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Data for the table `sys_role` */

insert  into `sys_role`(`id`,`name`,`code`,`perms`) values (1,'超级管理员','root','item_list,item_arranger,item_category,customer_joininfo,customer_list,customer_level,customer_order,sys_article,sys_config,sys_optlog,sys_resource,sys_role,sys_user');

/*Table structure for table `sys_user` */

DROP TABLE IF EXISTS `sys_user`;

CREATE TABLE `sys_user` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '主键id',
  `username` varchar(64) DEFAULT NULL COMMENT '用户名',
  `password` varchar(64) DEFAULT NULL COMMENT '密码',
  `realname` varchar(64) DEFAULT NULL COMMENT '姓名',
  `headimg` varchar(128) DEFAULT NULL COMMENT '头像',
  `status` varchar(32) DEFAULT NULL COMMENT '状态',
  `roles` varchar(64) DEFAULT NULL COMMENT '角色',
  `secret` varchar(128) DEFAULT NULL,
  `addtime` datetime DEFAULT NULL COMMENT '添加时间',
  `logintime` datetime DEFAULT NULL COMMENT '最后登录时间',
  `loginip` datetime DEFAULT NULL COMMENT '最后登录ip',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;

/*Data for the table `sys_user` */

insert  into `sys_user`(`id`,`username`,`password`,`realname`,`headimg`,`status`,`roles`,`secret`,`addtime`,`logintime`,`loginip`) values (1,'admin','admin123','admin',NULL,'正常','root',NULL,'2021-03-17 12:02:11','2021-04-06 17:17:12','2012-07-00 00:01:00');

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
