# --- Created by Ebean DDL
# To stop Ebean DDL generation, remove this comment and start using Evolutions

# --- !Ups

create table account (
  id                        bigint auto_increment not null,
  hash                      varchar(255),
  name                      varchar(255),
  date_create               TIMESTAMP DEFAULT NOW() not null,
  constraint pk_account primary key (id))
;

create table attribute (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  label                     varchar(255),
  constraint pk_attribute primary key (id))
;

create table log (
  id                        bigint auto_increment not null,
  server_id                 bigint,
  date_create               TIMESTAMP DEFAULT NOW() not null,
  constraint pk_log primary key (id))
;

create table log_attribute (
  id                        bigint auto_increment not null,
  log_id                    bigint,
  attribute_id              bigint,
  value                     double,
  constraint pk_log_attribute primary key (id))
;

create table period (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  length                    bigint,
  statistic_required        tinyint(1) default 0,
  constraint pk_period primary key (id))
;

create table server (
  id                        bigint auto_increment not null,
  name                      varchar(255),
  os_description            varchar(255),
  account_id                bigint,
  date_create               TIMESTAMP DEFAULT NOW() not null,
  constraint pk_server primary key (id))
;

create table server_attribute (
  id                        bigint auto_increment not null,
  value                     double,
  server_id                 bigint,
  attribute_id              bigint,
  constraint pk_server_attribute primary key (id))
;

create table setting (
  id                        varchar(255) not null,
  name                      varchar(255),
  value                     varchar(255),
  description               varchar(255),
  constraint pk_setting primary key (id))
;

create table statistic (
  id                        bigint auto_increment not null,
  server_id                 bigint,
  attribute_id              bigint,
  period_id                 bigint,
  avg_time                  bigint,
  avg_value                 double,
  values_count              bigint,
  constraint pk_statistic primary key (id))
;

alter table log add constraint fk_log_server_1 foreign key (server_id) references server (id) on delete restrict on update restrict;
create index ix_log_server_1 on log (server_id);
alter table log_attribute add constraint fk_log_attribute_log_2 foreign key (log_id) references log (id) on delete restrict on update restrict;
create index ix_log_attribute_log_2 on log_attribute (log_id);
alter table log_attribute add constraint fk_log_attribute_attribute_3 foreign key (attribute_id) references attribute (id) on delete restrict on update restrict;
create index ix_log_attribute_attribute_3 on log_attribute (attribute_id);
alter table server add constraint fk_server_account_4 foreign key (account_id) references account (id) on delete restrict on update restrict;
create index ix_server_account_4 on server (account_id);
alter table server_attribute add constraint fk_server_attribute_server_5 foreign key (server_id) references server (id) on delete restrict on update restrict;
create index ix_server_attribute_server_5 on server_attribute (server_id);
alter table server_attribute add constraint fk_server_attribute_attribute_6 foreign key (attribute_id) references attribute (id) on delete restrict on update restrict;
create index ix_server_attribute_attribute_6 on server_attribute (attribute_id);
alter table statistic add constraint fk_statistic_server_7 foreign key (server_id) references server (id) on delete restrict on update restrict;
create index ix_statistic_server_7 on statistic (server_id);
alter table statistic add constraint fk_statistic_attribute_8 foreign key (attribute_id) references attribute (id) on delete restrict on update restrict;
create index ix_statistic_attribute_8 on statistic (attribute_id);
alter table statistic add constraint fk_statistic_period_9 foreign key (period_id) references period (id) on delete restrict on update restrict;
create index ix_statistic_period_9 on statistic (period_id);



# --- !Downs

SET FOREIGN_KEY_CHECKS=0;

drop table account;

drop table attribute;

drop table log;

drop table log_attribute;

drop table period;

drop table server;

drop table server_attribute;

drop table setting;

drop table statistic;

SET FOREIGN_KEY_CHECKS=1;

