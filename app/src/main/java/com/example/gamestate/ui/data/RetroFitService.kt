package com.example.gamestate.ui.data

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface RetroFitService {
    @POST("/user/forgotpwd")
    fun sendEmail(@Body body: JsonObject): Call<ResponseBody>

    @POST("/user/register")
    fun register(@Body body: JsonObject): Call<ResponseBody>

    @POST("/user/login")
    fun login(@Body body: JsonObject): Call<ResponseBody>

    @POST("/game/search")
    fun sendGame(@Body body: JsonObject): Call<ResponseBody>

    @POST("/game/searchbyid")
    fun sendGameByID(@Body body: JsonObject): Call<ResponseBody>

    @POST("/topic/searchbyid")
    fun sendTopicByUser(@Body body: JsonObject): Call<ResponseBody>
}