package com.hhtv.eventqa.model.postmodel;

/**
 * Created by nienb on 15/3/16.
 */
public class Signin {
    private String username;
    private String password;

    public Signin(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
