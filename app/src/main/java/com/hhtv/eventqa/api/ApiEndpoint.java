package com.hhtv.eventqa.api;

import com.hhtv.eventqa.model.event.Event;
import com.hhtv.eventqa.model.event.EventDetail;
import com.hhtv.eventqa.model.question.Question;
import com.hhtv.eventqa.model.question.Vote;
import com.hhtv.eventqa.model.user.CreateUserResponse;
import com.hhtv.eventqa.model.user.GetUserResponse;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Created by nienb on 1/3/16.
 */
public interface ApiEndpoint {

    @GET("/api/test/GetEvents")
    Call<Event> getEvent(@Query("page") int page, @Query("pageSize") int pageSize);

    @GET("/api/test/GetQuestion")
    Call<Question> getQuestion(@Query("eventId") int eventId,@Query("userId") int userId,
                               @Query("page") int page,@Query("pageSize") int pageSize);

    @GET("/api/test/GetAllQuestions")
    Call<Question> getAllQuestions(@Query("eventId") int eventId,@Query("userId") int userId,@Query("deviceid") String deviceid);


    @GET("/api/test/GetQuestionByPivot")
    Call<Question> getQuestionByPivot(@Query("eventId") int eventId,@Query("userId") int userId,
                                      @Query("questionid") int questionid,@Query("up") boolean up);
    @GET("/api/test/Vote")
    Call<Vote> vote(@Query("voteId") int voteId, @Query("userId") int userId, @Query("up") boolean up
            ,@Query("lastCheckstr") String lastCheckstr);

    @GET("/api/test/updateQuestions")
    Call<Vote> updateQuestionsList(@Query("lastCheckstr") String lastCheckstr,
                                    @Query("eventid") int eventid,
                                    @Query("creatorid") int creatorid);

    @GET("/api/test/getEventDetail")
    Call<EventDetail> getEventDetail(@Query("eventid") String eventid);

    @GET("/api/test/getUser")
    Call<GetUserResponse> getUser(@Query("useremail") String useremail);

    @GET("/api/test/createUser")
    Call<CreateUserResponse> createUser(@Query("useremail") String useremail, @Query("name") String name);

    @GET("/api/test/createQuestion")
    Call<Vote> createQuestion(@Query("lastCheckstr") String lastCheckstr,@Query("eventid") int eventid, @Query("creatorid") int creatorid, @Query("body") String body);
}
