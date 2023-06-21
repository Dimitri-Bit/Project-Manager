package me.dimitri.project_manager.model.project;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class GroupID {

    private String groupId;

    public GroupID(String groupId) {
        this.groupId = groupId;
    }

    public GroupID() {
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
