package me.dimitri.project_manager.model.invite;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class InviteID {

    private String inviteId;

    public InviteID(String inviteId) {
        this.inviteId = inviteId;
    }

    public InviteID() {
        super();
    }

    public String getInviteId() {
        return inviteId;
    }

    public void setInviteId(String inviteId) {
        this.inviteId = inviteId;
    }
}
