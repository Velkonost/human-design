package com.myhumandesignhd.repo.nasa

import com.myhumandesignhd.model.Affirmation
import com.myhumandesignhd.model.CompatibilityResponse
import com.myhumandesignhd.model.Cycle
import com.myhumandesignhd.model.DailyAdvice
import com.myhumandesignhd.model.DesignChildResponse
import com.myhumandesignhd.model.Faq
import com.myhumandesignhd.model.Forecast
import com.myhumandesignhd.model.GetDesignResponse
import com.myhumandesignhd.model.TransitResponse
import com.myhumandesignhd.repo.base.RestRepo
import com.myhumandesignhd.rest.RestService
import com.myhumandesignhd.rest.RestServiceV2
import com.myhumandesignhd.util.ext.subscribeIoObserveMain
import io.reactivex.Single
import javax.inject.Inject

class RestRepoImpl @Inject constructor(
    private val restService: RestService
) : RestRepo {


    override fun getDesign(
        language: String,
        lat: String,
        lon: String,
        date: String
    ): Single<GetDesignResponse> =
        restService.getDesign(language, lat, lon, date)
            .subscribeIoObserveMain()

    override fun getTransit(
        language: String,
        lat: String,
        lon: String,
        date: String,
        currentDate: String
    ): Single<TransitResponse> =
        restService.getTransit(language, lat, lon, date, currentDate)
            .subscribeIoObserveMain()

    override fun getDesignChild(
        language: String,
        lat: String,
        lon: String,
        date: String
    ): Single<DesignChildResponse> =
        restService.getDesignChild(language, lat, lon, date)
            .subscribeIoObserveMain()

    override fun getCompatibility(
        language: String,
        lat: String,
        lon: String,
        date: String,
        lat1: String,
        lon1: String,
        date1: String
    ): Single<CompatibilityResponse> =
        restService.getCompatibility(language, lat, lon, date, lat1, lon1, date1)
            .subscribeIoObserveMain()

    override fun getAffirmations(): Single<List<Affirmation>> =
        restService.getAffirmations().subscribeIoObserveMain()

    override fun getForecasts(): Single<HashMap<String, List<Forecast>>> =
        restService.getForecasts().subscribeIoObserveMain()

    override fun getFaq(): Single<List<Faq>> =
        restService.getFaq().subscribeIoObserveMain()

    override fun geocoding(url: String) =
        restService.geocoding(url).subscribeIoObserveMain()

    override fun geocodingNominatim(url: String) =
        restService.geocodingNominatim(url).subscribeIoObserveMain()

    override fun getDailyAdvice(): Single<HashMap<String, List<DailyAdvice>>> =
        restService.getDailyAdvice().subscribeIoObserveMain()

    override fun getCycles(): Single<HashMap<String, List<Cycle>>> =
        restService.getCycles().subscribeIoObserveMain()

    override fun setUserInfo(
        url: String,
        gclid: String,
        appInstanceId: String
    ): Single<String> =
        restService.setUserInfo(url, gclid, appInstanceId).subscribeIoObserveMain()
}