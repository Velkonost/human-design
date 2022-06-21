package com.myhumandesignhd.rest

import com.myhumandesignhd.model.Affirmation
import com.myhumandesignhd.model.CompatibilityResponse
import com.myhumandesignhd.model.Cycle
import com.myhumandesignhd.model.DailyAdvice
import com.myhumandesignhd.model.DesignChildResponse
import com.myhumandesignhd.model.Faq
import com.myhumandesignhd.model.Forecast
import com.myhumandesignhd.model.GeocodingNominatimFeature
import com.myhumandesignhd.model.GeocodingResponse
import com.myhumandesignhd.model.GetDesignResponse
import com.myhumandesignhd.model.TransitResponse
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface RestService {

    //  Метод получения бодиграфа и описания типа человека,
    @GET("/getdesign.php")
    fun getDesign(
        @Query("language") language: String,
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("date") date: String
    ): Single<GetDesignResponse>

    //  Метод получения транзита на сегодня
    @GET("/transit.php")
    fun getTransit(
        @Query("language") language: String,
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("date") date: String,
        @Query("currentDate") currentDate: String
    ): Single<TransitResponse>

    //  Метод получения бодиграфа и описания типа ребенка
    @GET("/getdesign-child.php")
    fun getDesignChild(
        @Query("language") language: String,
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("date") date: String
    ): Single<DesignChildResponse>

    //  Метод получения совместимости 2ух бодиграфов
    @GET("/compatibility.php")
    fun getCompatibility(
        @Query("language") language: String,
        @Query("lat") lat: String,
        @Query("lon") lon: String,
        @Query("date") date: String,
        @Query("lat1") lat1: String,
        @Query("lon1") lon1: String,
        @Query("date1") date1: String,
    ): Single<CompatibilityResponse>

    //  Метод получения аффирмаций
    @GET("/affirmations/affirmationsJson.json")
    fun getAffirmations(): Single<List<Affirmation>>

    //  Метод получения прогнозов
    @GET("/forecasts/forecastsJson.json")
    fun getForecasts(): Single<HashMap<String, List<Forecast>>>

    //  Метод получения FAQ вопросов
    @GET("/getfaq.php")
    fun getFaq(): Single<List<Faq>>

    @GET
    fun geocoding(
        @Url url: String
    ): Single<GeocodingResponse>

    @GET
    fun geocodingNominatim(
        @Url url: String
    ): Single<List<GeocodingNominatimFeature>>

    @GET("/dailyadvice/dailyadvice.json")
    fun getDailyAdvice(): Single<HashMap<String, List<DailyAdvice>>>

    @GET("/info/cycles.json")
    fun getCycles(): Single<HashMap<String, List<Cycle>>>
//  Метод получения способа загрузки адрессов
//    @GET("/getapple.php")
//    fun getApple(): Single<>
}