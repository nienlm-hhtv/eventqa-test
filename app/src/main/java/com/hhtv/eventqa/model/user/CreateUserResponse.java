package com.hhtv.eventqa.model.user;

public class CreateUserResponse {

    private String status;
    private boolean success;
    private String msg;

    /**
     * No args constructor for use in serialization
     *
     */
    public CreateUserResponse() {
    }

    /**
     *
     * @param status
     * @param msg
     * @param success
     */
    public CreateUserResponse(String status, boolean success, String msg) {
        this.status = status;
        this.success = success;
        this.msg = msg;
    }

    /**
     *
     * @return
     * The status
     */
    public String getStatus() {
        return status;
    }

    /**
     *
     * @param status
     * The status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     *
     * @return
     * The success
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     *
     * @param success
     * The success
     */
    public void setSuccess(boolean success) {
        this.success = success;
    }

    /**
     *
     * @return
     * The msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     *
     * @param msg
     * The msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

}