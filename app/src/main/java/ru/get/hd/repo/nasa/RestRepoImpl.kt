package ru.get.hd.repo.nasa

import io.reactivex.Single
import ru.get.hd.model.Affirmation
import ru.get.hd.model.CompatibilityResponse
import ru.get.hd.model.DesignChildResponse
import ru.get.hd.model.Faq
import ru.get.hd.model.Forecast
import ru.get.hd.model.GetDesignResponse
import ru.get.hd.model.TransitResponse
import ru.get.hd.repo.base.RestRepo
import ru.get.hd.rest.RestService
import ru.get.hd.util.ext.subscribeIoObserveMain
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




}