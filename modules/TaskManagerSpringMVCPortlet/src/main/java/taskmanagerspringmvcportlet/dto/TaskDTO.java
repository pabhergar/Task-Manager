package taskmanagerspringmvcportlet.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

public class TaskDTO implements Serializable {

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public long getTaskId() {
        return taskId;
    }

    public void setTaskId(long taskId) {
        this.taskId = taskId;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private long taskId;
    @NotEmpty
    private String title;
    private String text;
    private boolean done;


}
