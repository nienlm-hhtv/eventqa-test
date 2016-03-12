
package com.hhtv.eventqa.model.user;

import java.util.HashMap;
import java.util.Map;

public class GetUserResponse {

    private Boolean success;
    private Integer code;
    private String useremail;
    private String username;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();


    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * No args constructor for use in serialization
     * 
     */
    public GetUserResponse() {
    }

    public GetUserResponse(Boolean success, Integer code, String useremail, String username) {
        this.success = success;
        this.code = code;
        this.useremail = useremail;
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
     *     The code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 
     * @param code
     *     The code
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
