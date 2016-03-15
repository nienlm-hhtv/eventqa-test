package com.hhtv.eventqa.model.postmodel;

/**
 * Created by nienb on 15/3/16.
 */
public class Signin {
    private String useremail;
    private String userpassword;

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getUserpassword() {
        return userpassword;
    }

    public void setUserpassword(String userpassword) {
        this.userpassword = userpassword;
    }

    public Signin(String useremail, String userpassword) {
        this.useremail = useremail;
        this.userpassword = userpassword;
    }
}
