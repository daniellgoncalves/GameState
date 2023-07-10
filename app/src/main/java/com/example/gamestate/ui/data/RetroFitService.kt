package com.example.gamestate.ui.data

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.*

interface RetroFitService {
    @POST("/users/forgotpwd")
    @Headers("Content-Type: application/json")
    fun sendEmail(@Body body: JsonObject): Call<ResponseBody>

    @POST("/users/register")
    @Headers("Content-Type: application/json")
    fun register(@Body body: JsonObject): Call<ResponseBody>

    @POST("/users/login")
    @Headers("Content-Type: application/json")
    fun login(@Body body: JsonObject): Call<ResponseBody>

    @GET("/games")
    @Headers("Content-Type: application/json")
    fun getPopularGames(@Header("Authorization") authorizationHeader: String, @Query("search") search: String, @Query("ordering") ordering: String): Call<ResponseBody>

    @GET("/games")
    @Headers("Content-Type: application/json")
    fun search(@Header("Authorization") authorizationHeader: String, @Query("search") search: String): Call<ResponseBody>

    @GET("/games/{id}")
    @Headers("Content-Type: application/json")
    fun searchByID(@Header("Authorization") authorizationHeader: String, @Path("id") id: Int): Call<ResponseBody>

    @PUT("/users/{id}")
    fun updateUserPushToken(@Header("Authorization") authorizationHeader: String, @Path("id") id: String, @Body body: JsonObject): Call<ResponseBody>

    @POST("/game/search")
    fun sendGame(@Body body: JsonObject): Call<ResponseBody>

    @POST("/game/searchbyid")
    @Headers("Content-Type: application/json")
    fun sendGameByID(@Header("Authorization") authorizationHeader: String, @Body body: JsonObject): Call<ResponseBody>

    @POST("/topics")
    @Headers("Content-Type: application/json")
    fun createTopic(@Header("Authorization") authorizationHeader: String, @Body body: JsonObject): Call<ResponseBody>

    @GET("/users/{username}/topics")
    @Headers("Content-Type: application/json")
    fun sendTopicByUser(@Header("Authorization") authorizationHeader: String, @Path("username") username: String): Call<ResponseBody>

    @GET("/topics/{id}")
    @Headers("Content-Type: application/json")
    fun sendTopicByID(@Header("Authorization") authorizationHeader: String, @Path("id") id: String): Call<ResponseBody>

    @POST("/topics/likedislike")
    @Headers("Content-Type: application/json")
    fun likeDislikeTopic(@Header("Authorization") authorizationHeader: String, @Body body: JsonObject): Call<ResponseBody>

    @POST("/topics/comments")
    @Headers("Content-Type: application/json")
    fun createcomment(@Header("Authorization") authorizationHeader: String, @Body body: JsonObject): Call<ResponseBody>

    @POST("/reviews/create")
    @Headers("Content-Type: application/json")
    fun createReview(@Header("Authorization") authorizationHeader: String, @Body body: JsonObject): Call<ResponseBody>

    @GET("/games/{id}/topics")
    @Headers("Content-Type: application/json")
    fun searchTopicByGameID(@Header("Authorization") authorizationHeader: String, @Path("id") id: Int): Call<ResponseBody>

    @GET("/users/{id}/subscribedgames")
    @Headers("Content-Type: application/json")
    fun getReviewsByUser(@Header("Authorization") authorizationHeader: String, @Path("id") id: String): Call<ResponseBody>

    @GET("/users/{id}/reviews")
    @Headers("Content-Type: application/json")
    fun findByUser(@Header("Authorization") authorizationHeader: String, @Path("id") id: String): Call<ResponseBody>

    @POST("/users/{id}/wishlist")
    @Headers("Content-Type: application/json")
    fun addGameToWishlist(@Header("Authorization") authorizationHeader: String, @Path("id") id: String, @Body body: JsonObject): Call<ResponseBody>

    @GET("/users/{id}/wishlistuser")
    @Headers("Content-Type: application/json")
    fun getWishlist(@Header("Authorization") authorizationHeader: String, @Path("id") id: String): Call<ResponseBody>

    @Multipart
    @POST("users/{id}/uploadprofilepicture") // Replace "upload" with the actual endpoint for image upload
    fun uploadImage(
        @Header("Authorization") authorizationHeader: String,
        @Path("id") id: String,
        @Part image: MultipartBody.Part
    ): Call<ResponseBody>

    @GET("/games/{id}/countries")
    @Headers("Content-Type: application/json")
    fun getCountries(@Header("Authorization") authorizationHeader: String, @Path("id") id: Int): Call<ResponseBody>
    @POST("/users/pushnotifications")
    fun sendPushNotification(@Header("Authorization") authorizationHeader: String, @Body body: JsonObject): Call<ResponseBody>

    @GET("/users/{id}")
    fun searchUserByID(@Header("Authorization") authorizationHeader: String, @Path("id") id: String): Call<ResponseBody>

    @GET("/reviews/searchbyid/{gameId}")
    @Headers("Content-Type: application/json")
    fun searchReviewsbyGameID(@Header("Authorization") authorizationHeader: String,@Path("gameId") id: Int): Call<ResponseBody>

    @POST("/reviews/searchbyuser")
    fun sendReviewByUser(@Body body: JsonObject): Call<ResponseBody>

    @POST("/reviews/getreviewsbyuser")
    fun getReviewsByUser(@Body body: JsonObject): Call<ResponseBody>
}