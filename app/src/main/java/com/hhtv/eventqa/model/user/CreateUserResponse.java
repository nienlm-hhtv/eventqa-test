
package com.hhtv.eventqa.model.user;

import java.util.HashMap;
import java.util.Map;

public class CreateUserResponse {

    private Boolean success;
    private Integer userid;
    private String useremail;
    private String username;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public CreateUserResponse() {
    }

    public CreateUserResponse(Boolean success, Integer userid, String useremail, String username) {
        this.success = success;
        this.userid = userid;
        this.useremail = useremail;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 
     * @return
     *     The success
     */
    public Boolean getSuccess() {
        return success;
    }

    /**
     * 
     * @param success
     *     The success
     */
    public void setSuccess(Boolean success) {
        this.success = success;
    }

    /**
     * 
     * @return
     *     The userid
     */
    public Integer getUserid() {
        return userid;
    }

    /**
     * 
     * @param userid
     *     The userid
     */
    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    /**
     * 
     * @return
     *     The useremail
     */
    public String getUseremail() {
        return useremail;
    }

    /**
     * 
     * @param useremail
     *     The useremail
     */
    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
