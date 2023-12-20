package com.myhumandesignhd.ui.start

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.myhumandesignhd.App
import com.myhumandesignhd.event.NoInetEvent
import com.myhumandesignhd.event.UpdateLoaderStateEvent
import com.myhumandesignhd.model.BodygraphChildrenData
import com.myhumandesignhd.model.BodygraphData
import com.myhumandesignhd.model.GeocodingNominatimFeature
import com.myhumandesignhd.model.GeocodingResponse
import com.myhumandesignhd.model.getDateStr
import com.myhumandesignhd.model.request.GoogleAccessTokenBody
import com.myhumandesignhd.model.response.LoginResponse
import com.myhumandesignhd.repo.base.RestRepo
import com.myhumandesignhd.repo.base.RestV2Repo
import com.myhumandesignhd.util.RxViewModel
import com.myhumandesignhd.util.SingleLiveEvent
import com.myhumandesignhd.util.ext.mutableLiveDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import javax.inject.Inject

class StartViewModel @Inject constructor(
    private val repo: RestRepo,
    private val repoV2: RestV2Repo
) : RxViewModel() {

    val errorEvent = SingleLiveEvent<Error>()

    var suggestions: MutableLiveData<GeocodingResponse> = mutableLiveDataOf(GeocodingResponse())
    var reverseSuggestions: MutableLiveData<List<GeocodingNominatimFeature>> =
        mutableLiveDataOf(emptyList())

    var nominatimSuggestions: MutableLiveData<List<GeocodingNominatimFeature>> =
        mutableLiveDataOf(emptyList())

    var loginFbLiveData: MutableLiveData<LoginResponse> = mutableLiveDataOf(LoginResponse())
    var loginEmailLiveData: MutableLiveData<LoginResponse> = mutableLiveDataOf(LoginResponse())

    var skipOnboarding: MutableLiveData<Boolean?> = mutableLiveDataOf(null)

    fun setupCurrentBodygraph() {
        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = true))

        repoV2.getAllBodygraphs().subscribe({ result ->
            val bodygraphs = result.data.toMutableList()

            val activeBodygraph = result.data.firstOrNull { it.isActive }
            if (activeBodygraph != null) {
                App.preferences.currentUserId = activeBodygraph.id
            }

            skipOnboarding.postValue(activeBodygraph != null && App.preferences.authToken.isNullOrEmpty())
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
        }, {
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            EventBus.getDefault().post(NoInetEvent())
        }).disposeOnCleared()
    }

    fun loginEmail(email: String, deviceId: String) {
        viewModelScope.launch {
            val bodygraphs = collectBodygraphsData()

            repoV2.loginEmail(email, deviceId, bodygraphs)
                .subscribe({
                    loginEmailLiveData.postValue(it)
                }, {
                    EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
                }).disposeOnCleared()
        }
    }

    fun checkLogin(deviceId: String, email: String) {
        repoV2.checkLogin(deviceId, email)
            .subscribe({
                loginFbLiveData.postValue(it)
                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            }, {
                loginFbLiveData.postValue(LoginResponse(status = "error", message = "User isn't logged in."))
                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            }).disposeOnCleared()
    }

    fun loginFb(accessToken: String) {
        viewModelScope.launch {
            val bodygraphs = collectBodygraphsData()

            repoV2.loginFb(accessToken, bodygraphs)
                .subscribe({
                    loginFbLiveData.postValue(it)
                }, {
                    EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
                }).disposeOnCleared()
        }
    }

    private fun loginGoogle(accessToken: String) {
        viewModelScope.launch {
            val bodygraphs = collectBodygraphsData()

            repoV2.loginGoogle(accessToken, bodygraphs)
                .subscribe({
                    loginFbLiveData.postValue(it)
                }, {
                    EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
                }).disposeOnCleared()
        }
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

    suspend fun isOldUser() =
        coroutineScope {
            withContext(Dispatchers.IO) {
                getAllUsers().isNotEmpty()
            }
        }

    suspend fun collectBodygraphsData() =
        coroutineScope {
            withContext(Dispatchers.IO) {
                val users = getAllUsers()
                val children = getAllChildren()

                val data = mutableListOf<BodygraphData>()

                users.map { user ->
                    val userChildren = mutableListOf<BodygraphChildrenData>()

                    children.filter { it.parentId == user.id }.map { child ->
                        userChildren.add(
                            BodygraphChildrenData(
                                name = child.name,
                                date = child.getDateStr(),
                                lat = child.lat,
                                lon = child.lon,
                            )
                        )
                    }

                    data.add(
                        BodygraphData(
                            name = user.name,
                            date = user.getDateStr(),
                            lat = user.lat,
                            lon = user.lon,
                            children = userChildren
                        )
                    )
                }

                data
            }
        }


    private suspend fun getAllUsers() =
        coroutineScope {
            withContext(Dispatchers.IO) {
                App.database.userDao().getAll()
            }
        }

    private suspend fun getAllChildren() =
        coroutineScope {
            withContext(Dispatchers.IO) {
                App.database.childDao().getAll()
            }
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