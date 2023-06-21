package me.dimitri.project_manager.model.project;

import io.micronaut.core.annotation.Introspected;

import java.util.List;

@Introspected
public class Task {
    private String id;
    private String taskGroupId;
    private String name;
    private String description;
    private String labelColor;
    private List<String> assignedEmails;
    private boolean finished;

    public Task(String id, String taskGroupId, String name, String description, String labelColor, List<String> assignedEmails, boolean finished) {
        this.id = id;
        this.taskGroupId = taskGroupId;
        this.name = name;
        this.description = description;
        this.labelColor = labelColor;
        this.assignedEmails = assignedEmails;
        this.finished = finished;
    }

    public Task() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTaskGroupId() {
        return taskGroupId;
    }

    public void setTaskGroupId(String taskGroupId) {
        this.taskGroupId = taskGroupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLabelColor() {
        return labelColor;
    }

    public void setLabelColor(String labelColor) {
        this.labelColor = labelColor;
    }

    public List<String> getAssignedEmails() {
        return assignedEmails;
    }

    public void setAssignedEmails(List<String> assignedEmails) {
        this.assignedEmails = assignedEmails;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }
}
