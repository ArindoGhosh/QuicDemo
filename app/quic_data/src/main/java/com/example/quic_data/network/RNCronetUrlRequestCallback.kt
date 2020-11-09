package com.example.quic_data.network

import android.os.ConditionVariable
import android.util.Log
import androidx.annotation.Nullable
import okhttp3.*
import org.chromium.net.CronetException
import org.chromium.net.UrlRequest
import org.chromium.net.UrlResponseInfo
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.nio.channels.WritableByteChannel


internal class RNCronetUrlRequestCallback(
    private val originalRequest: Request,
    private val mCall: Call,
    @Nullable private val eventListener: EventListener? = null,
    @Nullable private val responseCallback: Callback? = null
) : UrlRequest.Callback() {
    private var followCount = 0
    private var mResponse: Response = Response.Builder()
        .sentRequestAtMillis(System.currentTimeMillis())
        .request(originalRequest)
        .protocol(Protocol.HTTP_1_0)
        .code(0)
        .message("")
        .build()

    @Nullable
    private var mException: IOException? = null
    private val mResponseConditon = ConditionVariable()
    private val mBytesReceived: ByteArrayOutputStream = ByteArrayOutputStream()
    private val mReceiveChannel: WritableByteChannel = Channels.newChannel(mBytesReceived)

    @Throws(IOException::class)
    fun waitForDone(): Response {
        mResponseConditon.block()
        if (mException != null) {
            throw mException!!
        }
        return mResponse
    }

    override fun onRedirectReceived(
        request: UrlRequest,
        info: UrlResponseInfo,
        newLocationUrl: String
    ) {
        if (followCount > MAX_FOLLOW_COUNT) {
            request.cancel()
        }
        followCount += 1
        val client = OkHttpClient()
        if (originalRequest.url()
                .isHttps && newLocationUrl.startsWith("http://") && client.followSslRedirects()
        ) {
            request.followRedirect()
        } else if (!originalRequest.url()
                .isHttps && newLocationUrl.startsWith("https://") && client.followSslRedirects()
        ) {
            request.followRedirect()
        } else if (client.followRedirects()) {
            request.followRedirect()
        } else {
            request.cancel()
        }
    }

    override fun onResponseStarted(request: UrlRequest, info: UrlResponseInfo) {
        mResponse =
            responseFromResponse(
                mResponse,
                info
            )
        if (eventListener != null) {
            eventListener.responseHeadersEnd(mCall, mResponse)
            eventListener.responseBodyStart(mCall)
        }
        request.read(ByteBuffer.allocateDirect(32 * 1024))
    }

    @Throws(Exception::class)
    override fun onReadCompleted(
        request: UrlRequest,
        info: UrlResponseInfo?,
        byteBuffer: ByteBuffer
    ) {
        byteBuffer.flip()
        try {
            mReceiveChannel.write(byteBuffer)
        } catch (e: IOException) {
            Log.i(
                TAG,
                "IOException during ByteBuffer read. Details: ",
                e
            )
            throw e
        }
        byteBuffer.clear()
        request.read(byteBuffer)
    }

    override fun onSucceeded(request: UrlRequest, info: UrlResponseInfo) {
        eventListener?.responseBodyEnd(mCall, info.receivedByteCount)
        val contentTypeString: String? = mResponse.header("content-type")
        val contentType: MediaType? =
            MediaType.parse(contentTypeString ?: "text/plain; charset=\"utf-8\"")
        val responseBody: ResponseBody =
            ResponseBody.create(contentType, mBytesReceived.toByteArray())
        val newRequest: Request = originalRequest.newBuilder().url(info.url).build()
        mResponse = mResponse.newBuilder().body(responseBody).request(newRequest).build()
        mResponseConditon.open()
        eventListener?.callEnd(mCall)
        if (responseCallback != null) {
            try {
                responseCallback.onResponse(mCall, mResponse)
            } catch (e: IOException) {
                // Pass?
            }
        }
    }

    override fun onFailed(
        request: UrlRequest,
        info: UrlResponseInfo,
        error: CronetException
    ) {
        val e = IOException("Cronet Exception Occurred", error)
        mException = e
        mResponseConditon.open()
        if (eventListener != null) {
            eventListener.callFailed(mCall, e)
        }
        if (responseCallback != null) {
            responseCallback.onFailure(mCall, e)
        }
    }

    override fun onCanceled(request: UrlRequest, info: UrlResponseInfo) {
        mResponseConditon.open()
        if (eventListener != null) {
            eventListener.callEnd(mCall)
        }
    }

    companion object {
        private const val TAG = "Callback"
        private const val MAX_FOLLOW_COUNT = 20
        private fun protocolFromNegotiatedProtocol(responseInfo: UrlResponseInfo): Protocol {
            val negotiatedProtocol =
                responseInfo.negotiatedProtocol.toLowerCase()
            return if (negotiatedProtocol.contains("quic")) {
                Protocol.QUIC
            } else if (negotiatedProtocol.contains("h2")) {
                Protocol.HTTP_2
            } else if (negotiatedProtocol.contains("1.1")) {
                Protocol.HTTP_1_1
            } else {
                Protocol.HTTP_1_0
            }
        }

        private fun headersFromResponse(responseInfo: UrlResponseInfo): Headers {
            val headers =
                responseInfo.allHeadersAsList
            val headerBuilder: Headers.Builder = Headers.Builder()
            for ((key, value) in headers) {
                try {
                    if (key.equals("content-encoding", ignoreCase = true)) {
                        // Strip all content encoding headers as decoding is done handled by cronet
                        continue
                    }
                    headerBuilder.add(key, value)
                } catch (e: Exception) {
                    Log.w(
                        TAG,
                        "Invalid HTTP header/value: $key$value"
                    )
                    // Ignore that header
                }
            }
            return headerBuilder.build()
        }

        private fun responseFromResponse(
            response: Response,
            responseInfo: UrlResponseInfo
        ): Response {
            val protocol: Protocol =
                protocolFromNegotiatedProtocol(
                    responseInfo
                )
            val headers: Headers =
                headersFromResponse(
                    responseInfo
                )
            return response.newBuilder()
                .receivedResponseAtMillis(System.currentTimeMillis())
                .protocol(protocol)
                .code(responseInfo.httpStatusCode)
                .message(responseInfo.httpStatusText)
                .headers(headers)
                .build()
        }
    }
}