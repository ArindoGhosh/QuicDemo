package com.example.quicdemo.ui

import androidx.appcompat.app.AppCompatActivity
import com.example.quic_data.QuicDemoRepo
import com.example.quicdemo.QuicDemoApplication
import com.example.quicdemo.utility.ImageLoader

open class BaseActivity :AppCompatActivity(){
    protected val mQuicDemoRepo: QuicDemoRepo by lazy { (application as QuicDemoApplication).mQuicDemoRepo }
    protected val mImageLoader: ImageLoader by lazy { (application as QuicDemoApplication).mImageLoader }
}