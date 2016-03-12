
package com.hhtv.eventqa.model.changedquestion;

import com.hhtv.eventqa.model.question.Result;

import java.util.ArrayList;
import java.util.List;


public class ChangedQuestions {


    private Boolean success;
    private String message;
    private List<Result> changedQuestions = new ArrayList<Result>();
    private List<Result> newQuestions = new ArrayList<Result>();

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
     *     The changedQuestions
     */
    public List<Result> getChangedQuestions() {
        return changedQuestions;
    }

    /**
     * 
     * @param changedQuestions
     *     The changed_questions
     */
    public void setChangedQuestions(List<Result> changedQuestions) {
        this.changedQuestions = changedQuestions;
    }

    /**
     * 
     * @return
     *     The newQuestions
     */
    public List<Result> getNewQuestions() {
        return newQuestions;
    }

    /**
     * 
     * @param newQuestions
     *     The new_questions
     */
    public void setNewQuestions(List<Result> newQuestions) {
        this.newQuestions = newQuestions;
    }

}
