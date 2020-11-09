package com.example.quic_data.services

import com.example.quic_data.response.SampleResponse
import retrofit2.Response
import retrofit2.http.GET

interface SampleApi {
    @GET("users?page=1")
    suspend fun getSomeData():Response<SampleResponse>
}