package com.myhumandesignhd.repo.base

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

interface RestRepo {

    fun getDesign(
        language: String,
        lat: String,
        lon: String,
        date: String
    ): Single<GetDesignResponse>

    fun getTransit(
        language: String,
        lat: String,
        lon: String,
        date: String,
        currentDate: String
    ): Single<TransitResponse>

    fun getDesignChild(
        language: String,
        lat: String,
        lon: String,
        date: String
    ): Single<DesignChildResponse>

    fun getCompatibility(
        language: String,
        lat: String,
        lon: String,
        date: String,
        lat1: String,
        lon1: String,
        date1: String,
    ): Single<CompatibilityResponse>

    fun getAffirmations(): Single<List<Affirmation>>

    fun getForecasts(): Single<HashMap<String, List<Forecast>>>

    fun getFaq(): Single<List<Faq>>

    fun geocoding(url: String): Single<GeocodingResponse>
    fun geocodingNominatim(url: String): Single<List<GeocodingNominatimFeature>>

    fun getDailyAdvice(): Single<HashMap<String, List<DailyAdvice>>>
    fun getCycles(): Single<HashMap<String, List<Cycle>>>

    fun setUserInfo(
        url: String,
        gclid: String,
        appInstanceId: String
    ): Single<String>
}