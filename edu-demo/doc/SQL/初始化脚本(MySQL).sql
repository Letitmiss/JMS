drop table if exists edu_edmo_pms_user;

/*==============================================================*/
/* Table: edu_edmo_pms_user                                      */
/*==============================================================*/
create table edu_edmo_pms_user
(
   id                   bigint(20) not null auto_increment comment 'ID',
   version              int not null default 0 comment 'version',
   create_time          datetime not null default now() comment '创建时间',
   user_no              varchar(50) not null comment '用户帐号',
   user_type            varchar(1) not null comment '用户类型（1:超级管理员，2:普通管理员，3:用户主帐号，4:用户子帐号）',
   main_user_id         bigint(50) default 0 comment '主帐号ID',
   user_pwd             varchar(256) not null comment '登录密码',
   user_name            varchar(50) not null comment '姓名',
   mobile_no            varchar(15) comment '手机号',
   email                varbinary(100) comment '邮箱',
   status               int not null comment '状态(100:可用，101:不可用 )',
   last_login_time      datetime comment '最后登录时间',
   is_changed_pwd       int comment '是否更改过密码',
   pwd_error_count      int not null default 0 comment '连续输错密码次数',
   pwd_error_time       datetime comment '最后输错密码时间',
   remark               varchar(300) comment '备注',
   primary key (id),
   unique key AK_Key_2 (user_no)
);

alter table edu_edmo_pms_user comment '用户信息表';

## 用户的初始化数据(密码123456)
insert into edu_edmo_pms_user (id, user_no, user_pwd, remark, user_name, mobile_no, status, user_type, last_login_time, is_changed_pwd, pwd_error_count, pwd_error_time) values 
(1, 'admin', '7c4a8d09ca3762af61e59520943dc26494f8941b', '超级管理员', '超级管理员', '13800138000', '100', '1', null, 0, 0, null);