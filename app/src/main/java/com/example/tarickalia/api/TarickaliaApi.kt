package com.example.tarickalia.api

import com.google.gson.GsonBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.CoroutineContext
import retrofit2.Response
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class TarickaliaApi: CoroutineScope {
    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private val urlapi = "https://172.16.24.148:53198/api/"

    private fun getClient(): OkHttpClient {
        var login = HttpLoggingInterceptor()
        login.level = HttpLoggingInterceptor.Level.BODY

        return OkHttpClient.Builder().addInterceptor(login).build()
    }

    private fun getRetrofit(): Retrofit {
        val gson = GsonBuilder().setLenient().create()

        return Retrofit.Builder()
            .baseUrl(urlapi)
            .client(getUnsafeOkHttpClient())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    private fun getUnsafeOkHttpClient(): OkHttpClient {
        try {
            val trustAllCerts = arrayOf<TrustManager>(
                object : X509TrustManager {
                    override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit
                    override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) = Unit
                    override fun getAcceptedIssuers(): Array<X509Certificate> = arrayOf()
                }
            )

            val sslContext = SSLContext.getInstance("SSL")
            sslContext.init(null, trustAllCerts, java.security.SecureRandom())

            val sslSocketFactory = sslContext.socketFactory

            return OkHttpClient.Builder()
                .sslSocketFactory(sslSocketFactory, trustAllCerts[0] as X509TrustManager)
                .hostnameVerifier { _, _ -> true }
                .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
                .build()
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }

    fun getApiService(): ApiServices {
        return getRetrofit().create(ApiServices::class.java)
    }
}