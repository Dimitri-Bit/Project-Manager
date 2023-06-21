package me.dimitri.project_manager.model.project;

import io.micronaut.core.annotation.Introspected;

import java.util.List;

@Introspected
public class Project {

    private String id;
    private String name;
    private String ownerEmail;
    private List<String> teamMemberEmails;
    private String projectImage;

    public Project(String id, String name, String ownerEmail, List<String> teamMemberEmails, String projectImage) {
        this.id = id;
        this.name = name;
        this.ownerEmail = ownerEmail;
        this.teamMemberEmails = teamMemberEmails;
        this.projectImage = projectImage;
    }

    public Project() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerEmail() {
        return ownerEmail;
    }

    public void setOwnerEmail(String ownerEmail) {
        this.ownerEmail = ownerEmail;
    }

    public List<String> getTeamMemberEmails() {
        return teamMemberEmails;
    }

    public void setTeamMemberEmails(List<String> teamMemberEmails) {
        this.teamMemberEmails = teamMemberEmails;
    }

    public String getProjectImage() {
        return projectImage;
    }

    public void setProjectImage(String projectImage) {
        this.projectImage = projectImage;
    }
}
