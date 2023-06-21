package me.dimitri.project_manager.model.project;

import io.micronaut.core.annotation.Introspected;

import java.util.List;

@Introspected
public class RecommendUser {

    private String description;
    private List<String> users;

    public RecommendUser(String description, List<String> users) {
        this.description = description;
        this.users = users;
    }

    public RecommendUser() {
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
