package com.example.quic_data.network

import android.app.Application
import android.content.Context
import com.google.android.gms.net.CronetProviderInstaller
import okhttp3.*
import okio.Buffer
import org.chromium.net.CronetEngine
import org.chromium.net.UploadDataProviders
import org.chromium.net.UrlRequest
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class RNCronetNetworkingModule(private val mContext: Application) {
    private var mCronetEngine: CronetEngine? = null
    private val executorService: ExecutorService = Executors.newSingleThreadExecutor()

    init {
        CronetProviderInstaller.installProvider(mContext)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    initializeCronetEngine(mContext)
                }
            }
    }

    private fun getCronetEngine(context: Context): CronetEngine {
        return mCronetEngine ?: synchronized(this) {
            val cacheDir = File(context.cacheDir, "cronet-cache")
            cacheDir.mkdirs()
            val newInstance = CronetEngine.Builder(context)
                .enableHttpCache(CronetEngine.Builder.HTTP_CACHE_IN_MEMORY, 100 * 1024)
                .enableHttp2(true)
                .enableQuic(true)
                .setStoragePath(cacheDir.absolutePath)
                .enableHttpCache(
                    CronetEngine.Builder.HTTP_CACHE_DISK,
                    10 * 1024 * 1024.toLong()
                ) //
                .build()
                .also { mCronetEngine = it }
            newInstance
        }
    }

    @Synchronized
    private fun initializeCronetEngine(mContext: Context) {
        URL.setURLStreamHandlerFactory(getCronetEngine(mContext).createURLStreamHandlerFactory())
    }

    @Throws(IOException::class)
    fun buildRequest(request: Request, callback: UrlRequest.Callback?): UrlRequest? {
        val url: String = request.url().toString()
        val requestBuilder =
            mCronetEngine!!.newUrlRequestBuilder(url, callback, executorService)
        requestBuilder.setHttpMethod(request.method())
        val headers: Headers = request.headers()
        var i = 0
        while (i < headers.size()) {
            requestBuilder.addHeader(headers.name(i), headers.value(i))
            i += 1
        }
        val requestBody: RequestBody? = request.body()
        if (requestBody != null) {
            val contentType: MediaType? = requestBody.contentType()
            if (contentType != null) {
                requestBuilder.addHeader("Content-Type", contentType.toString())
            }
            val buffer = Buffer()
            requestBody.writeTo(buffer)
            requestBuilder.setUploadDataProvider(
                UploadDataProviders.create(buffer.readByteArray()),
                executorService
            )
        }
        return requestBuilder.build()
    }

    fun cronetEngine(): CronetEngine? {
        return mCronetEngine
    }
}