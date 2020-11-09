package com.example.quicdemo.utility

import android.app.Application
import android.widget.ImageView
import com.example.quic_data.network.RNCronetInterceptor
import com.example.quic_data.network.RNCronetNetworkModuletBuilder
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import okhttp3.OkHttpClient


class ImageLoader(context: Application) {
    private val mInstance: Picasso

    init {
        val okHttpClient = OkHttpClient()
            .newBuilder()
            .addInterceptor(
                RNCronetInterceptor(
                    RNCronetNetworkModuletBuilder.getRNCronetNetworkModuleInstance(context)
                )
            )
            .build()

        mInstance = Picasso.Builder(context)
            .downloader(OkHttp3Downloader(okHttpClient))
            .build()
    }

    fun loadInImageView(mImageView: ImageView, url: String) {
        mInstance
            .load(url)
            .into(mImageView)
    }
}