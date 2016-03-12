
package com.hhtv.eventqa.model.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Event {

    private Boolean success;
    private Integer total;
    private Integer pages;
    private Integer curPage;
    private List<Result> results = new ArrayList<Result>();
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     * 
     */
    public Event() {
    }

    /**
     * 
     * @param total
     * @param results
     * @param curPage
     * @param pages
     * @param success
     */
    public Event(Boolean success, Integer total, Integer pages, Integer curPage, List<Result> results) {
        this.success = success;
        this.total = total;
        this.pages = pages;
        this.curPage = curPage;
        this.results = results;
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
     *     The total
     */
    public Integer getTotal() {
        return total;
    }

    /**
     * 
     * @param total
     *     The total
     */
    public void setTotal(Integer total) {
        this.total = total;
    }

    /**
     * 
     * @return
     *     The pages
     */
    public Integer getPages() {
        return pages;
    }

    /**
     * 
     * @param pages
     *     The pages
     */
    public void setPages(Integer pages) {
        this.pages = pages;
    }

    /**
     * 
     * @return
     *     The curPage
     */
    public Integer getCurPage() {
        return curPage;
    }

    /**
     * 
     * @param curPage
     *     The curPage
     */
    public void setCurPage(Integer curPage) {
        this.curPage = curPage;
    }

    /**
     * 
     * @return
     *     The results
     */
    public List<Result> getResults() {
        return results;
    }

    /**
     * 
     * @param results
     *     The results
     */
    public void setResults(List<Result> results) {
        this.results = results;
    }

    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
