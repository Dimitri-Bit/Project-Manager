package me.dimitri.project_manager.model.project;

import io.micronaut.core.annotation.Introspected;

@Introspected
public class Member {

    private String username;
    private String name;
    private String image;

    public Member(String username, String name, String image) {
        this.username = username;
        this.name = name;
        this.image = image;
    }

    public Member() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
