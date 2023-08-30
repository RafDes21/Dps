package models

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {

    private fun getRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://ecu.dpsgo.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}