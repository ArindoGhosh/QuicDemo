package com.example.quic_data

import com.example.quic_data.response.SampleResponse
import kotlinx.coroutines.flow.Flow

interface QuicDemoRepo {
    fun getSomeData(): Flow<Result<SampleResponse>>
}