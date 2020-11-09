package com.example.quic_data.network

import android.app.Application

object RNCronetNetworkModuletBuilder {
    private var mRNCronetNetworkingModule: RNCronetNetworkingModule? = null
    fun getRNCronetNetworkModuleInstance(context: Application): RNCronetNetworkingModule {
        return mRNCronetNetworkingModule ?: synchronized(this) {
            val instance = RNCronetNetworkingModule(context).also { mRNCronetNetworkingModule = it }
            instance
        }
    }

}