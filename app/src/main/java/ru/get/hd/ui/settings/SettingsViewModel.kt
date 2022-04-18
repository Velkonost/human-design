package ru.get.hd.ui.settings

import androidx.lifecycle.MutableLiveData
import ru.get.hd.App
import ru.get.hd.model.GeocodingResponse
import ru.get.hd.repo.base.RestRepo
import ru.get.hd.util.RxViewModel
import ru.get.hd.util.SingleLiveEvent
import ru.get.hd.util.ext.mutableLiveDataOf
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
    private val repo: RestRepo
) : RxViewModel() {

    val errorEvent = SingleLiveEvent<Error>()

    var suggestions: MutableLiveData<GeocodingResponse> = mutableLiveDataOf(GeocodingResponse())

    fun geocoding(query: String?) {
        if (query.isNullOrEmpty()) {

        } else {
            repo.geocoding(
                "https://api.mapbox.com/geocoding/v5/mapbox.places/"
                        + query
                        + ".json?access_token=pk.eyJ1IjoidmVsa29ub3N0IiwiYSI6ImNsMXlxMWF6NjBmNWEzam1xazVzdm5lc3oifQ.MLuCuYBGTuf-u9RKme73lQ&language="
                        + App.preferences.locale
                        + "&autocomplete=true"
            ).subscribe({
                suggestions.postValue(it)
            }, {

            }).disposeOnCleared()
        }
    }
}