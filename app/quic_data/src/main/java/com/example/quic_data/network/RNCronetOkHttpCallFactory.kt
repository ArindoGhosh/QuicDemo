package com.example.quic_data.network

import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.internal.annotations.EverythingIsNonNull
import org.chromium.net.CronetEngine


/*
@EverythingIsNonNull
class RNCronetOkHttpCallFactory internal constructor(private val client: OkHttpClient) :
    Call.Factory {
    override fun newCall(request: Request?): Call {
        val engine: CronetEngine = RNCronetNetworkingModule.cronetEngine()
        return engine?.let { RNCronetOkHttpCall(client, it, request) } ?: client.newCall(request)
    }

}*/
