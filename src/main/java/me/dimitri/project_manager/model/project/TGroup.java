package me.dimitri.project_manager.model.project;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class TGroup {
    private String id;
    private String projectId;
    private String name;
    private String description;
    private String imageUrl;
    private String creatorEmail;

    public TGroup(String id, String projectId, String name, String description, String imageUrl, String creatorEmail) {
        this.id = id;
        this.projectId = projectId;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.creatorEmail = creatorEmail;
    }

    public TGroup() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getCreatorEmail() {
        return creatorEmail;
    }

    public void setCreatorEmail(String creatorEmail) {
        this.creatorEmail = creatorEmail;
    }
}
