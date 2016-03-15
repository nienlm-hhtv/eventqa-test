package com.hhtv.eventqa.model.postmodel;

/**
 * Created by nienb on 15/3/16.
 */
public class Signup {
    private String username, useremail, password;

    public Signup(String username, String useremail, String password) {
        this.username = username;
        this.useremail = useremail;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
