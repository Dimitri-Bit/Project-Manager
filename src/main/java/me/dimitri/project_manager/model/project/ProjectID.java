package me.dimitri.project_manager.model.project;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class ProjectID {

    private String projectId;

    public ProjectID(String projectId) {
        this.projectId = projectId;
    }

    public ProjectID() {
        super();
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
}
