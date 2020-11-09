package com.example.quicdemo.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.example.quic_data.QuicDemoRepo
import com.example.quic_data.Result
import com.example.quic_data.response.SampleResponse

class HomeViewModel:ViewModel() {
    fun getSampleResult(mQuicDemoRepo: QuicDemoRepo):LiveData<Result<SampleResponse>>{
        return mQuicDemoRepo
            .getSomeData()
            .asLiveData()
    }
}