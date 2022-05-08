package ru.get.hd.ui.start

import android.util.Log
import androidx.lifecycle.MutableLiveData
import org.greenrobot.eventbus.EventBus
import ru.get.hd.App
import ru.get.hd.event.NoInetEvent
import ru.get.hd.event.UpdateLoaderStateEvent
import ru.get.hd.model.GeocodingNominatimFeature
import ru.get.hd.model.GeocodingResponse
import ru.get.hd.model.GetDesignResponse
import ru.get.hd.repo.base.RestRepo
import ru.get.hd.util.RxViewModel
import ru.get.hd.util.SingleLiveEvent
import ru.get.hd.util.ext.mutableLiveDataOf
import javax.inject.Inject

class StartViewModel @Inject constructor(
    private val repo: RestRepo
) : RxViewModel() {

    val errorEvent = SingleLiveEvent<Error>()

    var suggestions: MutableLiveData<GeocodingResponse> = mutableLiveDataOf(GeocodingResponse())
    var reverseSuggestions: MutableLiveData<List<GeocodingNominatimFeature>> = mutableLiveDataOf(emptyList())

    var nominatimSuggestions: MutableLiveData<List<GeocodingNominatimFeature>> = mutableLiveDataOf(emptyList())

//    fun geocoding(query: String?) {
//        if (query.isNullOrEmpty()) {
//
//        } else {
//            repo.geocoding(
//                "https://api.mapbox.com/geocoding/v5/mapbox.places/"
//                        + query
//                        + ".json?access_token=pk.eyJ1IjoidmVsa29ub3N0IiwiYSI6ImNsMXlxMWF6NjBmNWEzam1xazVzdm5lc3oifQ.MLuCuYBGTuf-u9RKme73lQ&language="
//                        + App.preferences.locale
//                        + "&autocomplete=true"
//            ).subscribe({
//                suggestions.postValue(it)
//            }, {
//
//            }).disposeOnCleared()
//        }
//    }

    fun geocodingNominatim(query: String?) {
        if (query.isNullOrEmpty()) {
//https://nominatim.openstreetmap.org/search?q=%D0%BE%D0%BC%D1%81%D0%BA&format=json&accept-language=ru
        } else {
            repo.geocodingNominatim(
                "https://nominatim.openstreetmap.org/search?q="
                        + query
                        + "&format=json&accept-language="
                        + App.preferences.locale
                        + "&limit=50"
            ).subscribe({
//                suggestions.postValue(it)
                        nominatimSuggestions.postValue(it)
            }, {
                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
                EventBus.getDefault().post(NoInetEvent())
            }).disposeOnCleared()
        }
    }





//    fun reverseGeocoding(lat: Float, lon: Float) {
//        Log.d("keke", "keke")
//        repo.geocoding(
//            "https://api.mapbox.com/geocoding/v5/mapbox.places/$lat,$lon"
//                    + ".json?access_token=pk.eyJ1IjoidmVsa29ub3N0IiwiYSI6ImNsMXlxMWF6NjBmNWEzam1xazVzdm5lc3oifQ.MLuCuYBGTuf-u9RKme73lQ&language="
//                    + App.preferences.locale
//        ).subscribe({
//            reverseSuggestions.postValue(it)
//        }, {
//
//        }).disposeOnCleared()
//
//    }
}