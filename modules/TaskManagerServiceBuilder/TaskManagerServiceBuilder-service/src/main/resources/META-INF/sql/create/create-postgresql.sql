drop database lportal;
create database lportal encoding = 'UNICODE';
\c lportal;



create index IX_B54CBAC0 on TaskManager_Task (text_);
create index IX_2031A03C on TaskManager_Task (userId);
create index IX_4A5D972 on TaskManager_Task (uuid_);


