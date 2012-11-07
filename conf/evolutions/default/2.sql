# --- Sample dataset

# --- !Ups

insert into setting (id,name,value,description) values (  1,'minLogInterval','10','Minimal time interval in seconds before next log is accepted');
insert into setting (id,name,value,description) values (  2,'periodIntervals','30','Count of log intervals in selected period');

# --- !Downs

delete from setting;


# Add periods

# --- !Ups

insert into period (id,name,length,statistic_required) values (  1, 'Now', 3600,   0);
insert into period (id,name,length,statistic_required) values (  2, '8h',  28800,  1);
insert into period (id,name,length,statistic_required) values (  3, '1d',  86400,  1);
insert into period (id,name,length,statistic_required) values (  4, '7d',  604800, 1);
insert into period (id,name,length,statistic_required) values (  5, '30d', 2592000,1);

# --- !Downs

delete from period;