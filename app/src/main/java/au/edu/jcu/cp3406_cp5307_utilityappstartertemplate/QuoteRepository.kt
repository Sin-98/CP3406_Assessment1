package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

data class QuoteResponse(val q: String, val a: String)

interface QuoteApi {
    @GET("api/random")
    suspend fun getRandomQuote(): List<QuoteResponse>
}

object QuoteRepository {
    private val api = Retrofit.Builder()
        .baseUrl("https://zenquotes.io/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(QuoteApi::class.java)

    suspend fun fetchQuote(): String {
        return try {
            val result = api.getRandomQuote()
            "\"${result[0].q}\" — ${result[0].a}"
        } catch (e: Exception) {
            "Stay focused. Every minute counts."
        }
    }
}