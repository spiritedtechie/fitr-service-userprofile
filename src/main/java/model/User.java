package model;

import java.security.Principal;

public class User implements Principal {

    private String id;
    private String emailAddress;
    private String role;

    public User(String id, String emailAddress, String role) {
        this.id = id;
        this.emailAddress = emailAddress;
        this.role = role;
    }

    public String getId() {
        return id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String getName() {
        return id;
    }
}
