create table TaskManager_Task (
	uuid_ varchar(75) null,
	taskId bigint not null primary key,
	text_ varchar(75) null,
	userId bigint,
	done bool
);
