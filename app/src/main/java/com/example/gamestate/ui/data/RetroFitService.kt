package com.example.gamestate.ui.data

import com.google.gson.JsonObject
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST

interface RetroFitService {
    @POST("/user/forgotpwd")
    fun sendEmail(@Body body: JsonObject): Call<ResponseBody>
}