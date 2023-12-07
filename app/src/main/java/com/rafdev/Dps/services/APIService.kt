package com.rafdev.Dps.models

import retrofit2.Response
import retrofit2.http.GET

interface ApiService {

    @GET("streaming_test.json")
    suspend fun getData(): Response<ResponseModel>
}