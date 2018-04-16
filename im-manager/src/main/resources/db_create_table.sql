drop table if exists t_app;

drop table if exists t_app_user;

drop table if exists t_user;

/*==============================================================*/
/* Table: t_app                                                 */
/*==============================================================*/
create table t_app
(
   id                   int not null auto_increment comment '主键',
   name                 varchar(256) comment '业务系统名称',
   authorize_sip_count  int comment '授权SIP数量',
   authorize_call_count int comment '授权并发通话数量',
   expiry_date          date comment '授权过期时间',
   rsa_pub_key          varchar(2048) comment 'RSA公钥',
   rsa_private_key      varchar(2048) comment 'RSA私钥',
   aes_key              varchar(64) comment 'AES密码',
   primary key (id)
);

alter table t_app comment '接入业务系统';

/*==============================================================*/
/* Table: t_app_user                                            */
/*==============================================================*/
create table t_app_user
(
   id                   int not null auto_increment comment '主键',
   username             varchar(32) comment '用户名',
   password             varchar(32) comment '用户密码',
   nickname             varchar(64) comment '显示昵称',
   phone                varchar(18) comment '手机',
   token                varchar(64) comment '自动登录token',
   create_time          datetime comment '创建时间',
   primary key (id),
   unique key AK_Key_2 (username)
);

alter table t_app_user comment '内置业务用户表';

/*==============================================================*/
/* Table: t_user                                                */
/*==============================================================*/
create table t_user
(
   id                   int not null auto_increment comment '主键',
   app_uid              int comment '应用系统uid',
   nickname             varchar(45) comment '用户昵称',
   create_time          datetime comment '创建时间',
   last_login_time      time comment '最后登录时间',
   app_id               int comment '所属应用',
   primary key (id)
);

alter table t_user comment '系统登录用户';

alter table t_user add constraint FK_Reference_9 foreign key (app_id)
      references t_app (id) on delete restrict on update restrict;
