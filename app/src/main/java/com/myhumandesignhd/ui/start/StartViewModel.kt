package com.myhumandesignhd.ui.start

import androidx.lifecycle.MutableLiveData
import com.myhumandesignhd.App
import com.myhumandesignhd.event.NoInetEvent
import com.myhumandesignhd.event.UpdateLoaderStateEvent
import com.myhumandesignhd.model.GeocodingNominatimFeature
import com.myhumandesignhd.model.GeocodingResponse
import com.myhumandesignhd.model.request.GoogleAccessTokenBody
import com.myhumandesignhd.model.response.LoginEmailResponse
import com.myhumandesignhd.model.response.LoginResponse
import com.myhumandesignhd.repo.base.RestRepo
import com.myhumandesignhd.repo.base.RestV2Repo
import com.myhumandesignhd.util.RxViewModel
import com.myhumandesignhd.util.SingleLiveEvent
import com.myhumandesignhd.util.ext.mutableLiveDataOf
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class StartViewModel @Inject constructor(
    private val repo: RestRepo,
    private val repoV2: RestV2Repo
) : RxViewModel() {

    val errorEvent = SingleLiveEvent<Error>()

    var suggestions: MutableLiveData<GeocodingResponse> = mutableLiveDataOf(GeocodingResponse())
    var reverseSuggestions: MutableLiveData<List<GeocodingNominatimFeature>> = mutableLiveDataOf(emptyList())

    var nominatimSuggestions: MutableLiveData<List<GeocodingNominatimFeature>> = mutableLiveDataOf(emptyList())

    var loginFbLiveData: MutableLiveData<LoginResponse> = mutableLiveDataOf(LoginResponse())
    var loginEmailLiveData: MutableLiveData<LoginEmailResponse> = mutableLiveDataOf(
        LoginEmailResponse()
    )

    fun loginEmail(email: String) {
        repoV2.loginEmail(email)
            .subscribe({
                loginEmailLiveData.postValue(it)
            }, {
                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            }).disposeOnCleared()
    }

    fun loginFb(accessToken: String) {
        repoV2.loginFb(accessToken)
            .subscribe({
                loginFbLiveData.postValue(it)
            }, {
                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            }).disposeOnCleared()
    }

    fun loginGoogle(accessToken: String) {
        repoV2.loginGoogle(accessToken)
            .subscribe({
                loginFbLiveData.postValue(it)
            }, {
                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            }).disposeOnCleared()
    }

    fun getGoogleAccessToken(authCode: String) {
        repoV2.getGoogleAccessToken(GoogleAccessTokenBody(code = authCode))
            .subscribe({
                if (it.accessToken.isNullOrEmpty().not()) {
                    loginGoogle(it.accessToken)
                }
            }, {
                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            }).disposeOnCleared()
    }

    fun geocodingNominatim(query: String?) {
        if (query.isNullOrEmpty()) {
        } else {
            val acceptLang = if (App.preferences.locale == "es") "en" else App.preferences.locale

            repo.geocodingNominatim(
                "https://nominatim.openstreetmap.org/search?q="
                        + query
                        + "&format=json&accept-language="
                        + acceptLang
                        + "&limit=50"
            ).subscribe({
                 nominatimSuggestions.postValue(it)
            }, {
                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
                EventBus.getDefault().post(NoInetEvent())
            }).disposeOnCleared()
        }
    }



}