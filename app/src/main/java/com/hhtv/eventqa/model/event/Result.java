
package com.hhtv.eventqa.model.event;

import java.util.HashMap;
import java.util.Map;

public class Result {

    private int id;
    private String createAt;
    private String name;
    private String url;
    private String description;
    private String imageLink;
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Result() {
    }

    /**
     * 
     * @param id
     * @param createAt
     * @param description
     * @param imageLink
     * @param name
     * @param url
     */
    public Result(int id, String createAt, String name, String url, String description, String imageLink) {
        this.id = id;
        this.createAt = createAt;
        this.name = name;
        this.url = url;
        this.description = description;
        this.imageLink = imageLink;
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
     *     The createAt
     */
    public String getCreateAt() {
        return createAt;
    }

    /**
     * 
     * @param createAt
     *     The create_at
     */
    public void setCreateAt(String createAt) {
        this.createAt = createAt;
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
     *     The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * 
     * @param url
     *     The url
     */
    public void setUrl(String url) {
        this.url = url;
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
     *     The imageLink
     */
    public String getImageLink() {
        return imageLink;
    }

    /**
     * 
     * @param imageLink
     *     The image_link
     */
    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
