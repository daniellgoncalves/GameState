package com.example.gamestate.ui.data

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface RetroFitService {
    @POST("/user/forgotpwd")
    fun sendEmail(@Body body: JsonObject): Call<ResponseBody>

    @POST("/user/register")
    fun register(@Body body: JsonObject): Call<ResponseBody>

    @POST("/user/login")
    fun login(@Body body: JsonObject): Call<ResponseBody>

    @PUT("/user/{userID}")
    fun updateUserPushToken(@Path("userID") id: String, @Body body: JsonObject): Call<ResponseBody>

    @POST("/game/search")
    fun sendGame(@Body body: JsonObject): Call<ResponseBody>

    @POST("/game/searchbyid")
    fun sendGameByID(@Body body: JsonObject): Call<ResponseBody>

    @POST("/topic/create")
    fun createTopic(@Body body: JsonObject): Call<ResponseBody>

    @POST("/topic/searchbyid")
    fun sendTopicByUser(@Body body: JsonObject): Call<ResponseBody>

    @POST("/topic/searchbytopicid")
    fun sendTopicByID(@Body body: JsonObject): Call<ResponseBody>

    @POST("/topic/likedislike")
    fun likeDislikeTopic(@Body body: JsonObject): Call<ResponseBody>

    @POST("/topic/createcomment")
    fun createcomment(@Body body: JsonObject): Call<ResponseBody>

    @POST("/reviews/create")
    fun createReview(@Body body: JsonObject): Call<ResponseBody>

    @GET("/topic/searchbygameid/{gameId}")
    fun searchTopicByGameID(@Path("gameId") id: Int): Call<ResponseBody>

    @POST("/fcm/send")
    fun sendPushNotification(@Body body: JsonObject, @Header("Content-Type") type: String, @Header("Authorization") key: String): Call<ResponseBody>

    @GET("/user/{userID}")
    fun searchUserByID(@Path("userID") id: String): Call<ResponseBody>

    @GET("/reviews/searchbyid/{gameId}")
    fun searchReviewsbyGameID(@Path("gameId") id: Int): Call<ResponseBody>

    @POST("/reviews/searchbyuser")
    fun sendReviewByUser(@Body body: JsonObject): Call<ResponseBody>

    @POST("/reviews/getreviewsbyuser")
    fun getReviewsByUser(@Body body: JsonObject): Call<ResponseBody>
}