package com.example.quic_data

import android.app.Application
import com.example.quic_data.exceptions.SomethingWentWrongException
import com.example.quic_data.response.SampleResponse
import com.example.quic_data.services.SampleApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class QuicDemoRepoImpl(private val mContext: Application) : QuicDemoRepo {
    private val mServiceFactory by lazy { ServiceFactoryImpl(mContext) }
    private val mSampleApiService: SampleApi by lazy { mServiceFactory.getSampleService("https://reqres.in/api/") } //todo add base url
    override fun getSomeData(): Flow<Result<SampleResponse>> {
        return flow {
            emit(Result.Loading)
            val response = mSampleApiService.getSomeData()
            if (response.isSuccessful && response.body() != null) {
                emit(Result.Success(response.body()!!))
            } else {
                emit(Result.Error(SomethingWentWrongException()))
            }
        }
    }
}