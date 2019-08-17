package com.pro_crafting.tools.githubjwtIntegration.jwt.service.model;

import java.util.List;

public class JWTUser {
    private String userName;

    private String[] roles;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String[] getRoles() {
        return roles;
    }

    public void setRoles(String[] roles) {
        this.roles = roles;
    }
}
