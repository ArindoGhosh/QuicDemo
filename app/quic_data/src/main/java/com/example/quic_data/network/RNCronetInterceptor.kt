package com.example.quic_data.network

import okhttp3.Call
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import okhttp3.internal.annotations.EverythingIsNonNull
import org.chromium.net.UrlRequest
import java.io.IOException


@EverythingIsNonNull
class RNCronetInterceptor(private val mRNCronetNetworkingModule: RNCronetNetworkingModule) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (mRNCronetNetworkingModule.cronetEngine() != null) {
            proceedWithCronet(chain.request(), chain.call())
        } else {
            chain.proceed(chain.request())
        }
    }

    @Throws(IOException::class)
    private fun proceedWithCronet(request: Request, call: Call): Response {
        val callback =
            RNCronetUrlRequestCallback(
                request,
                call
            )
        val urlRequest: UrlRequest? = mRNCronetNetworkingModule.buildRequest(request, callback)
        urlRequest?.start()
        return callback.waitForDone()
    }

}
