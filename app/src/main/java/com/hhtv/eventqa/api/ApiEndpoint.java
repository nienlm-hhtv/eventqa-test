package com.hhtv.eventqa.api;

import com.hhtv.eventqa.model.event.Event;
import com.hhtv.eventqa.model.event.EventDetail;
import com.hhtv.eventqa.model.question.Question;
import com.hhtv.eventqa.model.question.Vote;
import com.hhtv.eventqa.model.user.CreateUserResponse;
import com.hhtv.eventqa.model.user.GetUserResponse;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
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

    @GET("/api/event/getQuestions")
    Call<Question> getAllQuestions(@Query("event_id") int eventId,@Query("user_id") int userId,@Query("device_id") String deviceid);



    @GET("/api/event/voteQuestion")
    Call<Vote> vote(@Query("event_id") int eventId, @Query("question_id") int voteId, @Query("user_id") int userId, @Query("vote_status") boolean up
            ,@Query("device_id") String lastCheckstr);

    @GET("/api/event/getUpdatedQuestions")
    Call<Vote> updateQuestionsList(@Query("device_id") String lastCheckstr,
                                    @Query("event_id") int eventid,
                                    @Query("user_id") int creatorid);

    @GET("/api/event/getEventDetail")
    Call<EventDetail> getEventDetail(@Query("id") String eventid);

    @GET("/api/test/getUser")
    Call<GetUserResponse> getUser(@Query("useremail") String useremail);

    @GET("/api/test/createUser")
    Call<CreateUserResponse> createUser(@Query("useremail") String useremail, @Query("name") String name);

    @GET("/api/test/createQuestion")
    Call<Vote> createQuestion(@Query("lastCheckstr") String lastCheckstr,@Query("eventid") int eventid, @Query("creatorid") int creatorid, @Query("body") String body);


    @GET("/api/event/getHighestVoteQuestions")
    Call<Question> getHighestVoteQuestion(@Query("event_id") int eventId,@Query("user_id")
        int userId,@Query("device_id") String deviceid);

    @FormUrlEncoded
    @POST("/api/event/getUser/")
    Call<GetUserResponse> signin(@Field("username") String username, @Field("password") String password);

    @FormUrlEncoded
    @POST("/api/event/registerUser/")
    Call<CreateUserResponse> signup(@Field("username") String username, @Field("email") String email);

    @GET("/api/test/GetAllQuestions")
    Call<Question> getAllFakeQuestions(
            @Query("eventId") int eventId,
            @Query("userId") int userId,
            @Query("deviceid") String deviceid);
    @GET("/api/test/updateQuestions")
    Call<Vote> updateFakeQuestionsList(@Query("lastCheckstr") String lastCheckstr,
                                   @Query("eventid") int eventid,
                                   @Query("creatorid") int creatorid);
    @GET("/api/test/getEventDetail")
    Call<EventDetail> getFakeEventDetail(@Query("eventid") String eventid);

}
