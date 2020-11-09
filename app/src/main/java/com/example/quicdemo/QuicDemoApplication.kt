package com.example.quicdemo

import android.app.Application
import com.example.quic_data.QuicDemoRepo
import com.example.quic_data.QuicDemoRepoImpl
import com.example.quicdemo.utility.ImageLoader

class QuicDemoApplication : Application() {
    lateinit var mQuicDemoRepo: QuicDemoRepo
    lateinit var mImageLoader: ImageLoader
    override fun onCreate() {
        super.onCreate()
        mQuicDemoRepo = QuicDemoRepoImpl(this)
        mImageLoader = ImageLoader(this)
    }
}