insert into smms.t_depart (depart_name) values ('PC制');

insert into smms.t_permission (permission_code,permission_desc,role_id) values
	 ('user_button_instore','客户端- 入库按钮',1),
	 ('user_button_virus','客户端 - 杀毒按钮',1),
	 ('user_button_print','客户端 - 打印按钮',1),
	 ('user_button_output','客户端 - 导出按钮',1),
	 ('user_button_edit','客户端- 编辑按钮',1),
	 ('user_button_borrow','客户端- 借出按钮',1),
	 ('user_button_scrap','客户端 - 报废按钮',1),
	 ('user_button_delete','客户端 - 删除按钮',1),
	 ('user_button_show','客户端 - 揭示板按钮',1),
	 ('user_menu_process','客户端 - 流程中心',1),
	 ('user_menu_main','客户端 - 主菜单',1),
	 ('admin_menu_user','后台中心 - 用户管理',1),
	 ('admin_menu_role','后台中心 - 角色管理',1);
insert into smms.t_permission (permission_code,permission_desc,role_id) values
	 ('admin_menu_depart','后台中心 - 部门管理',1),
	 ('admin_menu_work_line','后台中心 - 班线别管理',1),
--	 ('admin_menu_post','后台中心 - 岗位管理',1),
	 ('admin_menu_process','后台中心 -流程中心',1),
	 ('admin_menu_process2','后台中心 -流程中心(Admin)',1),
	 ('admin_menu_main','后台中心 - 主菜单',1);


insert into smms.t_post (post_name) values
	 ('M'),
	 ('OP'),
	 ('TEC'),
	 ('ENG'),
	 ('S'),
	 ('AM'),
	 ('SCS');


insert into smms.t_role (role_desc,role_name) values
	 ('超级管理员','Super-Admin'),
	 ('设备管理员','Device-Admin'),
	 ('设备所属部门课长','Manager'),
	 ('普通用户','Common-User');


insert into smms.t_section (section_name,department_id) values
	 ('TPM',1);


INSERT INTO smms.t_user (depart_id,duties,icons,mail,password,role_id,section_id,sex,status,user_name,user_no) VALUES
	 (1,'1','default.png','super.admin@dbh.dynabook.com','e10adc3949ba59abbe56e057f20f883e',1,1,'男','在职','admin','admin'),
     (2, 1, 'default.png', 'lin.zhu@dbh.dynabook.com', 'e10adc3949ba59abbe56e057f20f883e', 4, 1, '男', '在职', '朱林', '501411'),
     (3, 1, 'default.png', 'min.wang@dbh.dynabook.com', 'e10adc3949ba59abbe56e057f20f883e', 4, 1, '女', '在职', '王敏', '127159'),
     (4, 1, 'default.png', 'device.admin@dbh.dynabook.com', 'e10adc3949ba59abbe56e057f20f883e', 2, 1, '男', '在职', '设备管理员', 'device'),
     (5, 1, 'default.png', 'manager@dbh.dynabook.com', 'e10adc3949ba59abbe56e057f20f883e', 3, 1, '男', '在职', '课长', 'manager'),
     (6, 1, 'default.png', 'common.user@dbh.dynabook.com', 'e10adc3949ba59abbe56e057f20f883e', 4, 1, '男', '在职', '普通用户', 'common');