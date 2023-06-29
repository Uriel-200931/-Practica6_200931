package mx.edu.utxj.tidgs.ddi.practica6_200931.presentation

import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {
    @GET("weather")
    suspend fun getWeather(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("appid") apiKey: String
    ): Response<WeatherData>
}
