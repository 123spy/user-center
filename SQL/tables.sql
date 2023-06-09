create database `user-center`;

use `user-center`;

-- auto-generated definition
create table user
(
    id           bigint auto_increment primary key,
    userAccount  varchar(256)                       null,
    username     varchar(256)                       null,
    avatarUrl    varchar(1024)                      null,
    userPassword varchar(512)                       null,
    phone        varchar(128)                       null,
    email        varchar(512)                       null,
    userStatus   int      default 0                 not null comment '帐号状态 0正常 1异常',
    userRole     varchar(255)   default 'user'      null comment '用户角色：user普通用户 admin管理员',
    createTime   datetime default CURRENT_TIMESTAMP null,
    updateTime   datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    isDelete     tinyint  default 0                 not null
);

