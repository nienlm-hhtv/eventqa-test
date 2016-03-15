
package com.hhtv.eventqa.model.event;


import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class EventDetail implements Serializable{

    private Boolean success;
    private String message;
    private int id;
    private String name;
    private String description;
    private int creator_id;
    private String creator_name;
    private String create_at;
    private int total_question;
    private int answered_question;
    private int status;
    private String imageLink;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public EventDetail() {
    }

    /**
     * 
     * @param id
     * @param message
     * @param total_question
     * @param create_at
     * @param creator_name
     * @param status
     * @param description
     * @param imageLink
     * @param name
     * @param creator_id
     * @param answered_question
     * @param success
     */
    public EventDetail(Boolean success, String message, int id, String name, String description, String imageLink, int creator_id, String creator_name, String create_at, int total_question, int answered_question, int status) {
        this.success = success;
        this.message = message;
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageLink = imageLink;
        this.creator_id = creator_id;
        this.creator_name = creator_name;
        this.create_at = create_at;
        this.total_question = total_question;
        this.answered_question = answered_question;
        this.status = status;
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
     *     The message
     */
    public String getMessage() {
        return message;
    }

    /**
     * 
     * @param message
     *     The message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * 
     * @return
     *     The id
     */
    public int getId() {
        return id;
    }

    /**
     * 
     * @param id
     *     The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * 
     * @return
     *     The name
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     *     The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     *     The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * 
     * @param description
     *     The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     */
    public String getImageLink() {
        return imageLink;
    }

    /**
     *
     * @param imageLink
     */
    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    /**
     * 
     * @return
     *     The creator_id
     */
    public int getcreator_id() {
        return creator_id;
    }

    /**
     * 
     * @param creator_id
     *     The creator_id
     */
    public void setcreator_id(int creator_id) {
        this.creator_id = creator_id;
    }

    /**
     * 
     * @return
     *     The creator_name
     */
    public String getcreator_name() {
        return creator_name;
    }

    /**
     * 
     * @param creator_name
     *     The creator_name
     */
    public void setcreator_name(String creator_name) {
        this.creator_name = creator_name;
    }

    /**
     * 
     * @return
     *     The create_at
     */
    public String getcreate_at() {
        return create_at;
    }

    /**
     * 
     * @param create_at
     *     The create_at
     */
    public void setcreate_at(String create_at) {
        this.create_at = create_at;
    }

    /**
     * 
     * @return
     *     The total_question
     */
    public int gettotal_question() {
        return total_question;
    }

    /**
     * 
     * @param total_question
     *     The total_question
     */
    public void settotal_question(int total_question) {
        this.total_question = total_question;
    }

    /**
     * 
     * @return
     *     The answered_question
     */
    public int getanswered_question() {
        return answered_question;
    }

    /**
     * 
     * @param answered_question
     *     The answered_question
     */
    public void setanswered_question(int answered_question) {
        this.answered_question = answered_question;
    }

    /**
     * 
     * @return
     *     The status
     */
    public int getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     *     The status
     */
    public void setStatus(int status) {
        this.status = status;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
