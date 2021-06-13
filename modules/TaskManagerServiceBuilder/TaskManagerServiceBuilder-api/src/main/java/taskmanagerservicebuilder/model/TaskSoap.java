/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package taskmanagerservicebuilder.model;

import java.io.Serializable;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is used by SOAP remote services, specifically {@link taskmanagerservicebuilder.service.http.TaskServiceSoap}.
 *
 * @author Brian Wing Shun Chan
 * @generated
 */
public class TaskSoap implements Serializable {

	public static TaskSoap toSoapModel(Task model) {
		TaskSoap soapModel = new TaskSoap();

		soapModel.setUuid(model.getUuid());
		soapModel.setTaskId(model.getTaskId());
		soapModel.setTitle(model.getTitle());
		soapModel.setText(model.getText());
		soapModel.setUserId(model.getUserId());
		soapModel.setDone(model.isDone());

		return soapModel;
	}

	public static TaskSoap[] toSoapModels(Task[] models) {
		TaskSoap[] soapModels = new TaskSoap[models.length];

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModel(models[i]);
		}

		return soapModels;
	}

	public static TaskSoap[][] toSoapModels(Task[][] models) {
		TaskSoap[][] soapModels = null;

		if (models.length > 0) {
			soapModels = new TaskSoap[models.length][models[0].length];
		}
		else {
			soapModels = new TaskSoap[0][0];
		}

		for (int i = 0; i < models.length; i++) {
			soapModels[i] = toSoapModels(models[i]);
		}

		return soapModels;
	}

	public static TaskSoap[] toSoapModels(List<Task> models) {
		List<TaskSoap> soapModels = new ArrayList<TaskSoap>(models.size());

		for (Task model : models) {
			soapModels.add(toSoapModel(model));
		}

		return soapModels.toArray(new TaskSoap[soapModels.size()]);
	}

	public TaskSoap() {
	}

	public long getPrimaryKey() {
		return _taskId;
	}

	public void setPrimaryKey(long pk) {
		setTaskId(pk);
	}

	public String getUuid() {
		return _uuid;
	}

	public void setUuid(String uuid) {
		_uuid = uuid;
	}

	public long getTaskId() {
		return _taskId;
	}

	public void setTaskId(long taskId) {
		_taskId = taskId;
	}

	public String getTitle() {
		return _title;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public String getText() {
		return _text;
	}

	public void setText(String text) {
		_text = text;
	}

	public long getUserId() {
		return _userId;
	}

	public void setUserId(long userId) {
		_userId = userId;
	}

	public boolean getDone() {
		return _done;
	}

	public boolean isDone() {
		return _done;
	}

	public void setDone(boolean done) {
		_done = done;
	}

	private String _uuid;
	private long _taskId;
	private String _title;
	private String _text;
	private long _userId;
	private boolean _done;

}