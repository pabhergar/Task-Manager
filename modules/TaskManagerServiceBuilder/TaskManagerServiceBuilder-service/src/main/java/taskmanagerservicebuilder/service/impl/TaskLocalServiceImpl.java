/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 * <p>
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * <p>
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package taskmanagerservicebuilder.service.impl;

import com.liferay.portal.aop.AopService;
import org.osgi.service.component.annotations.Component;

import taskmanagerservicebuilder.exception.NoSuchTaskException;
import taskmanagerservicebuilder.model.Task;
import taskmanagerservicebuilder.model.impl.TaskImpl;
import taskmanagerservicebuilder.service.base.TaskLocalServiceBaseImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of the task local service.
 *
 * <p>
 * All custom service methods should be put in this class. Whenever methods are added, rerun ServiceBuilder to copy their definitions into the <code>taskmanagerservicebuilder.service.TaskLocalService</code> interface.
 *
 * <p>
 * This is a local service. Methods of this service will not have security checks based on the propagated JAAS credentials because this service can only be accessed from within the same VM.
 * </p>
 *
 * @author Brian Wing Shun Chan
 * @see TaskLocalServiceBaseImpl
 */
@Component(
        property = "model.class.name=taskmanagerservicebuilder.model.Task",
        service = AopService.class
)
public class TaskLocalServiceImpl extends TaskLocalServiceBaseImpl {

    /*
     * NOTE FOR DEVELOPERS:
     *
     * Never reference this class directly. Use <code>taskmanagerservicebuilder.service.TaskLocalService</code> via injection or a <code>org.osgi.util.tracker.ServiceTracker</code> or use <code>taskmanagerservicebuilder.service.TaskLocalServiceUtil</code>.
     */
    public List<Task> getUserTasks(long userId) {
        List<Task> taskList = taskPersistence.findByuserFinder(userId);
        return taskList;
    }

    public Task createTask(String title, String text, long userId) {

        long taskId = counterLocalService.increment(Task.class.getName());

        Task task = taskPersistence.create(taskId);

        task.setTitle(title);
        task.setText(text);
        task.setUserId(userId);

        super.addTask(task);

        return task;
    }

    public Task editTask(long taskId, String title, String text, Boolean done) throws NoSuchTaskException {
        Task task = taskPersistence.findByPrimaryKey(taskId);
        task.setTitle(title);
        task.setText(text);
        task.setDone(done);
        taskPersistence.update(task);
        return task;
    }

    public Task doTask(long taskId) throws NoSuchTaskException {
        Task task = taskPersistence.findByPrimaryKey(taskId);
        task.setDone(true);
        taskPersistence.update(task);
        return task;
    }

    public Task undoTask(long taskId) throws NoSuchTaskException {
        Task task = taskPersistence.findByPrimaryKey(taskId);
        task.setDone(false);
        taskPersistence.update(task);
        return task;
    }

    public Task deleteTask(long taskId) throws NoSuchTaskException {
        return taskPersistence.remove(taskId);
    }

}