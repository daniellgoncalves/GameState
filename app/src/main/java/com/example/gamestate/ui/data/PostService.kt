package com.example.gamestate.ui.data

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

////http://localhost:3000/user/login

interface PostService
{
    @POST("/user/register")
    fun register(@Body body: JsonObject): Call<ResponseBody>
}