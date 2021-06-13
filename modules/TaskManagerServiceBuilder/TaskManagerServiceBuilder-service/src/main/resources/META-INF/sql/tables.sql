create table TaskManager_Task (
	uuid_ VARCHAR(75) null,
	taskId LONG not null primary key,
	title VARCHAR(75) null,
	text_ VARCHAR(75) null,
	userId LONG,
	done BOOLEAN
);