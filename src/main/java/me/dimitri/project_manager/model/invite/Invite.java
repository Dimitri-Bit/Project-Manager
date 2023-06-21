package me.dimitri.project_manager.model.invite;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Invite {

    private String id;
    private String projectId;
    private String projectName;
    private String inviterEmail;
    private String invitedEmail;

    public Invite(String id, String projectId, String projectName, String inviterEmail, String invitedEmail) {
        this.id = id;
        this.projectId = projectId;
        this.projectName = projectName;
        this.inviterEmail = inviterEmail;
        this.invitedEmail = invitedEmail;
    }

    public Invite() {
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

    public String getInviterEmail() {
        return inviterEmail;
    }

    public void setInviterEmail(String inviterEmail) {
        this.inviterEmail = inviterEmail;
    }

    public String getInvitedEmail() {
        return invitedEmail;
    }

    public void setInvitedEmail(String invitedEmail) {
        this.invitedEmail = invitedEmail;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
}
