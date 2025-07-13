package com.ilya.rickandmorty.api

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Log.d("RetrofitLog", "Request URL: ${request.url}")

        val response = chain.proceed(request)

        // Читаем тело ответа для логировани
        val responseBody = response.peekBody(Long.MAX_VALUE).string()
        Log.d("RetrofitLog", "Response body: $responseBody")

        return response
    }
}
