package me.dimitri.project_manager.model.project;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class TaskID {

    private String taskId;

    public TaskID(String taskId) {
        this.taskId = taskId;
    }

    public TaskID() {
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
}
