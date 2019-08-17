package com.pro_crafting.tools.githubjwtIntegration.jwt.rest.model;

public class AuthenticationImportData {
    private String userName;

    private String personalAccessToken;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPersonalAccessToken() {
        return personalAccessToken;
    }

    public void setPersonalAccessToken(String personalAccessToken) {
        this.personalAccessToken = personalAccessToken;
    }
}
