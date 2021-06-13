package taskmanagerspringmvcportlet.dto;

import taskmanagerservicebuilder.model.Task;

import java.util.List;

public class TaskList {
    public List<TaskDTO> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<TaskDTO> taskList) {
        this.taskList = taskList;
    }

    private List<TaskDTO> taskList;
}
