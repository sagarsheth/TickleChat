package com.techpro.chat.ticklechat.rest;

import com.techpro.chat.ticklechat.models.GetGroupDetails;
import com.techpro.chat.ticklechat.models.message.AllMessages;
import com.techpro.chat.ticklechat.models.message.SendMessage;
import com.techpro.chat.ticklechat.models.message.Tickles;
import com.techpro.chat.ticklechat.models.user.GetUserDetails;
import com.techpro.chat.ticklechat.models.user.UserModel;

import retrofit2.Call;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;


public interface ApiInterface {

    //Get response in GSON object
    @GET("users/{userid}")
    Call<GetUserDetails> getUserDetailsFromID(@Path("userid") int userId);
    //Get response in GSON object

    @GET("messages/all")
    Call<AllMessages> getAllMessageList();

    @GET("tickles")
    Call<Tickles> getTickles();

    @GET("groups/{groupId}")
    Call<GetGroupDetails> getGroupDetials(@Path("groupId") int groupId);

    @FormUrlEncoded
    @POST("messages")
    Call<SendMessage> postChatMessage(@Field("tickleId") String tickleId, @Field("withId") String withId, @Field("message") String message);

    @FormUrlEncoded
    @POST("users/login")
    Call<UserModel> loginUser(@Field("phone") String phone, @Field("password") String password);

    @FormUrlEncoded
    @PUT("users/{userid}/status")
    Call<UserModel> statusUpdate(@Path("userid") int userId, @Field("status") String status);

    @FormUrlEncoded
    @PUT("users/{userid}")
    Call<UserModel> updateUserDetails(@Path("userid") int userId, @Field("name") String name,
                                      @Field("gender") String gender, @Field("dob") String dob,
                                      @Field("phone") String phone, @Field("email") String email,
                                      @Field("profile_image") String profile_image);

    @DELETE("users/{userid}")
    Call<UserModel> deleteUser(@Path("userid") int userId);


    /*//Get response in json string
    @GET("users/{userid}")
    Call<ResponseBody> getUserDetails(@Path("userid") int chatUserList);*/

}
