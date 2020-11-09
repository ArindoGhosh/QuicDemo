package com.example.quic_data

import android.app.Application
import com.example.quic_data.network.RetrofitBuilder
import com.example.quic_data.services.SampleApi

interface ServiceFactory {
    fun getSampleService(baseUrl: String): SampleApi
}

class ServiceFactoryImpl(private val context: Application) : ServiceFactory {
    override fun getSampleService(baseUrl: String): SampleApi {
        return RetrofitBuilder.getRetrofitBuilder(context, baseUrl).create(SampleApi::class.java)
    }
}