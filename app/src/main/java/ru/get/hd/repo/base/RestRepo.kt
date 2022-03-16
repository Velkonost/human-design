package ru.get.hd.repo.base

import io.reactivex.Single
import retrofit2.http.Query
import ru.get.hd.model.Affirmation
import ru.get.hd.model.CompatibilityResponse
import ru.get.hd.model.DesignChildResponse
import ru.get.hd.model.Faq
import ru.get.hd.model.Forecast
import ru.get.hd.model.GetDesignResponse
import ru.get.hd.model.TransitResponse

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

    fun getForecasts(): Single<List<List<Forecast>>>

    fun getFaq(): Single<List<Faq>>
}