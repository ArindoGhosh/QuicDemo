package com.example.quic_data.network

import android.app.Application
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitBuilder {

    fun getRetrofitBuilder(context: Application, baseUrl: String): Retrofit {
        val okHttpClient = OkHttpClient()
            .newBuilder()
            .addInterceptor(
                RNCronetInterceptor(
                    RNCronetNetworkModuletBuilder.getRNCronetNetworkModuleInstance(context)
                )
            )
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}

