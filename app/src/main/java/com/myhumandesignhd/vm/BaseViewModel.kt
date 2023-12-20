package com.myhumandesignhd.vm

import androidx.lifecycle.MutableLiveData
import com.myhumandesignhd.App
import com.myhumandesignhd.event.CurrentUserLoadedEvent
import com.myhumandesignhd.event.NoInetEvent
import com.myhumandesignhd.event.SetInjuryAlarmEvent
import com.myhumandesignhd.event.SetupNotificationsEvent
import com.myhumandesignhd.event.UpdateLoaderStateEvent
import com.myhumandesignhd.model.Affirmation
import com.myhumandesignhd.model.CompatibilityResponse
import com.myhumandesignhd.model.Cycle
import com.myhumandesignhd.model.DailyAdvice
import com.myhumandesignhd.model.Faq
import com.myhumandesignhd.model.Forecast
import com.myhumandesignhd.model.GeocodingNominatimFeature
import com.myhumandesignhd.model.TransitResponse
import com.myhumandesignhd.model.User
import com.myhumandesignhd.model.response.BodygraphResponse
import com.myhumandesignhd.model.response.SubscriptionStatusData
import com.myhumandesignhd.repo.base.RestRepo
import com.myhumandesignhd.repo.base.RestV2Repo
import com.myhumandesignhd.rest.ResponseStatus
import com.myhumandesignhd.util.RxViewModel
import com.myhumandesignhd.util.SingleLiveEvent
import com.myhumandesignhd.util.ViewState
import com.myhumandesignhd.util.ext.mutableLiveDataOf
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.threeten.bp.Instant
import org.threeten.bp.ZoneId
import org.threeten.bp.format.DateTimeFormatter
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject
import kotlin.math.floor


class BaseViewModel @Inject constructor(
    private val repo: RestRepo, private val repoV2: RestV2Repo
) : RxViewModel() {

    val errorEvent = SingleLiveEvent<String>()

    val currentUserSetupEvent = SingleLiveEvent<Boolean>()

    lateinit var currentUser: User

    var currentCompatibility: MutableLiveData<CompatibilityResponse> = mutableLiveDataOf(
        CompatibilityResponse()
    )
    var currentBodygraph: MutableLiveData<BodygraphResponse> =
        mutableLiveDataOf(BodygraphResponse())
    var currentPartnerBodygraph: MutableLiveData<BodygraphResponse> = mutableLiveDataOf(
        BodygraphResponse()
    )
    var currentChildBodygraph: MutableLiveData<BodygraphResponse> = mutableLiveDataOf(
        BodygraphResponse()
    )

    var allBodygraphsData: MutableLiveData<List<BodygraphResponse>> = mutableLiveDataOf(emptyList())
    var childrenData: MutableLiveData<List<BodygraphResponse>> = mutableLiveDataOf(emptyList())

    var currentAffirmation: MutableLiveData<Affirmation> = mutableLiveDataOf(Affirmation())
    var affirmations: MutableList<Affirmation> = mutableListOf()

    var currentForecast: MutableLiveData<Forecast> = mutableLiveDataOf(Forecast())
    var currentDailyAdvice: MutableLiveData<DailyAdvice> = mutableLiveDataOf(DailyAdvice())
    var currentCycles: MutableLiveData<List<Cycle>> = mutableLiveDataOf(emptyList())

    var currentTransit: MutableLiveData<TransitResponse> = mutableLiveDataOf(TransitResponse())

    val faqsList: MutableList<Faq> = mutableListOf()

    val updateUsersEvent = SingleLiveEvent<Boolean>()

    private var bodygraphs: MutableList<BodygraphResponse> = mutableListOf()

    var injuryStatus: InjuryStatus = InjuryStatus.NOT_STARTED
    var injuryPercent: Int? = null
    var injuryRemain: Long? = null

    var subscriptionData: MutableLiveData<SubscriptionStatusData?> = mutableLiveDataOf(null)

    val verifyEmailState = mutableLiveDataOf<ViewState<String>>(ViewState.Idle)

    init {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    private var isUserLoaded = false
    private var isBodygraphLoaded = false
    private var isAffirmationLoaded = false
    private var isForecastLoaded = false
    private var isTransitLoaded = false
    private var isDailyAdviceLoaded = false
    private var isCyclesLoaded = false

    fun verifyEmail(deviceId: String) {
        if (App.preferences.authToken == null) {
            verifyEmailState.postValue(ViewState.Data("error"))
            return
        }

        if (App.preferences.authToken != null && App.preferences.authSignature == null) {
            verifyEmailState.postValue(ViewState.Data("success"))
            return
        }

        repoV2.verifyEmail(
            deviceId = deviceId,
            expires = App.preferences.authExpires ?: "",
            id = App.preferences.authId ?: "",
            signature = App.preferences.authSignature ?: "",
            token = App.preferences.authToken ?: ""
        ).subscribe({
            verifyEmailState.postValue(ViewState.Data(it.status))
            App.preferences.authSignature = null
        }, {
            verifyEmailState.postValue(ViewState.Data("error"))
        }).disposeOnCleared()
    }

    fun setUserInfo(gclid: String, appInstanceId: String) {
        repo.setUserInfo("http://5.45.79.21/setuserinfo.php", gclid, appInstanceId)
            .subscribe({}, {}).disposeOnCleared()
    }

    fun cancelStripeSubscription() {
        repoV2.cancelStripeSubscription()
            .subscribe({
                subscriptionData.postValue(it.data)
            }, {

            }).disposeOnCleared()
    }

    fun checkSubscription() {
        App.preferences.authToken?.let {
            repoV2.checkSubscriptionStatus().subscribe({
                it.data?.let { data ->
                    subscriptionData.postValue(data)
                    App.preferences.isPremiun = data.isActive
                }

            }, {

            }).disposeOnCleared()
        }
    }

    private fun resetAllUserDataStates() {
        isUserLoaded = false
        isBodygraphLoaded = false
        isAffirmationLoaded = false
        isForecastLoaded = false
        isTransitLoaded = false
        isDailyAdviceLoaded = false
        isCyclesLoaded = false
    }

    fun updatePushForecastPosition() {
        currentUser.pushForecastPosition++
        updateUser()
    }

    private fun checkIsUserDataLoaded() {
        if (
            isUserLoaded
            && isBodygraphLoaded
//            && isAffirmationLoaded
//            && isForecastLoaded
            && isTransitLoaded
            && isDailyAdviceLoaded
        ) {
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            EventBus.getDefault().post(CurrentUserLoadedEvent())

//            EventBus.getDefault().post(ShowHelpEvent(type = HelpType.BodygraphCenters))

            EventBus.getDefault().post(SetupNotificationsEvent())
        }
    }

    fun setupCompatibility(id: String, onComplete: (Int) -> Unit) {

        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = true))

        repoV2.getCompatibility(id = id, light = false).subscribe({
            var percentageAvg = 0
            it.data.newDescriptions.map { nd ->
                percentageAvg += nd.percentage
            }
            percentageAvg /= it.data.newDescriptions.size

            onComplete.invoke(percentageAvg)

            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            currentCompatibility.postValue(it)
        }, {
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
//            EventBus.getDefault().post(NoInetEvent())
        }).disposeOnCleared()
    }

    fun getAllBodygraphs() {
        if (App.preferences.authToken.isNullOrEmpty()) return

        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = true))
        repoV2.getAllBodygraphs()
            .subscribe({
                if (it.status == ResponseStatus.SUCCESS.title) {
                    if (it.data.isNotEmpty()) {
                        bodygraphs = it.data.toMutableList()
                        allBodygraphsData.postValue(it.data)
                    }
                }
                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            }, {
                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            }).disposeOnCleared()
    }

    fun editBodygraph(
        name: String? = null,
        date: String? = null,
        lat: String? = null,
        lon: String? = null
    ) {
        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = true))

        val activeBodygraph = bodygraphs.first { it.isActive }
        repoV2.editBodygraph(
            bodygraphId = activeBodygraph.id,
            name = name, date = date, lat = lat, lon = lon,
        ).subscribe({
            setupCurrentUser()
        }, {
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
        }).disposeOnCleared()

    }

    private fun setupCurrentBodygraph() {
        if (App.preferences.authToken.isNullOrEmpty()) return
        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = true))

        repoV2.getAllBodygraphs().subscribe({ result ->
            bodygraphs = result.data.toMutableList()

            val activeBodygraph = result.data.first { it.isActive }
            App.preferences.isUserLoginBodygraphSetup = true
            getBodygraphPlace(activeBodygraph.lat, activeBodygraph.lon)

            currentBodygraph.postValue(activeBodygraph)

            if (activeBodygraph.children != null) {
                childrenData.postValue(activeBodygraph.children)
            }

            isBodygraphLoaded = true
            checkIsUserDataLoaded()

            currentCycles.postValue(activeBodygraph.cycles)
            setupCurrentTransit()

            setupCurrentForecast(activeBodygraph.birthDatetime)
            setupCurrentAffirmation(activeBodygraph.birthDatetime)
            setupCurrentDailyAdvice(activeBodygraph.birthDatetime)
            getInjuryStatus()

        }, {
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            EventBus.getDefault().post(NoInetEvent())
        }).disposeOnCleared()
    }

    private fun setupCurrentTransit() {
        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = true))

        val formatter: DateFormat = SimpleDateFormat(App.DATE_FORMAT, Locale.getDefault())
        val calendar: Calendar = Calendar.getInstance()

        calendar.timeInMillis = System.currentTimeMillis()
        val currentDateStr = formatter.format(calendar.time)

        repoV2.getTransit(currentDateStr).subscribe({
            currentTransit.postValue(it)

            isTransitLoaded = true
            checkIsUserDataLoaded()
        }, {
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            EventBus.getDefault().post(NoInetEvent())
        }).disposeOnCleared()
    }

    private fun setupCurrentForecast(userDate: String) {
        repoV2.getForecast(userDate)
            .subscribe({
                currentForecast.postValue(it.data)
                isForecastLoaded = true
                checkIsUserDataLoaded()

            }, {
                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
                EventBus.getDefault().post(NoInetEvent())
            }).disposeOnCleared()
    }

    private fun setupCurrentDailyAdvice(userDate: String) {
        repoV2.getDailyAdvice(userDate)
            .subscribe({
                isDailyAdviceLoaded = true
                checkIsUserDataLoaded()
                currentDailyAdvice.postValue(it.data)
            }, {
                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
                EventBus.getDefault().post(NoInetEvent())
            }).disposeOnCleared()

    }

    private fun setupCurrentAffirmation(userDate: String) {
        repoV2.getAffirmation(userDate)
            .subscribe({
                currentAffirmation.postValue(it.data)
                isAffirmationLoaded = true
                checkIsUserDataLoaded()
            }, {
            }).disposeOnCleared()
    }

    private fun setupPartnerBodygraph(
        name: String, lat: String, lon: String, dateStr: String, isActive: Boolean = false
    ) {
        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = true))

        repoV2.createBodygraph(
            name = name,
            lat = lat,
            lon = lon,
            date = dateStr,
            isActive = isActive,
            isChild = false
        ).subscribe({ result ->
            val activeBodygraph = result.data.first { it.isActive }

            App.preferences.currentUserId = activeBodygraph.id

            currentPartnerBodygraph.postValue(activeBodygraph)
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))

            if (isActive) {
                setupCurrentUser()
            }

        }, {
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            EventBus.getDefault().post(NoInetEvent())
        }).disposeOnCleared()
    }

    private fun setupChildBodygraph(name: String, lat: String, lon: String, dateStr: String) {
        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = true))

        repoV2.createBodygraph(
            name = name,
            lat = lat,
            lon = lon,
            date = dateStr,
            isActive = false,
            isChild = true
        ).subscribe({ result ->
            val activeBodygraph = result.data.first { it.isActive }

            if (activeBodygraph.children != null) {
                childrenData.postValue(activeBodygraph.children)
            }

            currentChildBodygraph.postValue(activeBodygraph)
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))

        }, {
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            EventBus.getDefault().post(NoInetEvent())
        }).disposeOnCleared()
    }

    fun createNewUser(
        name: String, place: String, date: Long, time: String, lat: String, lon: String,
        fromCompatibility: Boolean = false, fromDiagram: Boolean = false
    ) {
        if (App.preferences.authToken.isNullOrEmpty()) return
        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = true))

        val hours = time.split(":")[0]
        val minutes = time.split(":")[1]

        val cal = Calendar.getInstance()

        val localDataFormatter = DateTimeFormatter.ofPattern(App.DATE_FORMAT_SHORT)
        val localDate: String =
            Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate()
                .format(localDataFormatter)

        val days =
            if (date < 0) floor(date.toDouble() / 86400000)
            else floor(date.toDouble() / 86400000).toInt()

        cal.timeInMillis = (days.toLong()) * 86400000L

        val datetime = "$localDate $hours:$minutes:00"

        repoV2.getAllBodygraphs()
            .subscribe({ result ->
                val activeBodygraph = result.data.firstOrNull { it.isActive }

                if (activeBodygraph != null && !fromCompatibility && !fromDiagram) {
                    App.preferences.currentUserId = activeBodygraph.id

                    //edit
                    repoV2.editBodygraph(
                        bodygraphId = activeBodygraph.id, name = name.trim(),
                        lat = lat, lon = lon, date = datetime
                    ).subscribe({
                        setupCurrentUser()
                    }, {
                        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
                    }).disposeOnCleared()
                } else {
                    createNewBodygraph(name, place, datetime, lat, lon, fromCompatibility)
                }
            }, {
                if (it is NoSuchElementException) {
                    createNewBodygraph(name, place, datetime, lat, lon, fromCompatibility)
                }
            }).disposeOnCleared()
    }

    private fun createNewBodygraph(
        name: String, place: String, datetime: String, lat: String, lon: String,
        fromCompatibility: Boolean = false
    ) {
        if (!fromCompatibility) {
            setupPartnerBodygraph(name.trim(), lat, lon, datetime, isActive = true)
        } else {
            setupPartnerBodygraph(name.trim(), lat, lon, datetime)
        }
    }

    fun createNewChild(
        name: String, place: String, date: Long, time: String, lat: String, lon: String
    ) {
        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = true))

        val hours = time.split(":")[0]
        val minutes = time.split(":")[1]

        val cal = Calendar.getInstance()

        val localDataFormatter = DateTimeFormatter.ofPattern(App.DATE_FORMAT_SHORT)
        val localDate: String =
            Instant.ofEpochMilli(date).atZone(ZoneId.systemDefault()).toLocalDate()
                .format(localDataFormatter)

        val days =
            if (date < 0) floor(date.toDouble() / 86400000)
            else floor(date.toDouble() / 86400000).toInt()

        cal.timeInMillis = (days.toLong()) * 86400000L

        val datetime = "$localDate $hours:$minutes:00"

        setupChildBodygraph(name.trim(), lat, lon, datetime)
    }

    fun changeActiveBodygraph(newActiveId: Long) {
        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = true))

        repoV2.editBodygraph(
            bodygraphId = newActiveId,
            isActive = true
        ).subscribe({ result ->
            val activeBodygraph = result.data.first { it.isActive }

            App.preferences.currentUserId = activeBodygraph.id
            setupCurrentUser()
        }, {
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
        }).disposeOnCleared()
    }

    fun setupCurrentUser() {
        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = true))
        resetAllUserDataStates()

        checkSubscription()
        setupCurrentBodygraph()

        currentUserSetupEvent.postValue(true)

        isUserLoaded = true
        checkIsUserDataLoaded()
    }

    fun deleteUser(userIdToDelete: Long, onComplete: () -> Unit) {
        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = true))

        repoV2.deleteBodygraph(userIdToDelete)
            .subscribe({ result ->
                bodygraphs = result.data.toMutableList()

                val activeBodygraph = bodygraphs.first { it.isActive }
                currentBodygraph.postValue(activeBodygraph)

                if (activeBodygraph.children != null) {
                    childrenData.postValue(activeBodygraph.children)
                }

                allBodygraphsData.postValue(result.data)

                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
                onComplete.invoke()
            }, {
                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            }).disposeOnCleared()
    }

    fun deleteChild(childIdToDelete: Long, onComplete: () -> Unit) {
        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = true))

        repoV2.deleteBodygraph(childIdToDelete)
            .subscribe({ result ->
                bodygraphs = result.data.toMutableList()

                val activeBodygraph = bodygraphs.first { it.isActive }
                currentBodygraph.postValue(activeBodygraph)

                if (activeBodygraph.children != null) {
                    childrenData.postValue(activeBodygraph.children)
                }

                allBodygraphsData.postValue(result.data)

                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
                onComplete.invoke()
            }, {
                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            }).disposeOnCleared()
    }

    fun startInjury() {
        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = true))

        repoV2.startInjury()
            .subscribe({
                injuryStatus = when (it.data.status) {
                    InjuryStatus.NOT_STARTED.title -> InjuryStatus.NOT_STARTED
                    InjuryStatus.STARTED.title -> InjuryStatus.STARTED
                    else -> InjuryStatus.FINISHED
                }
                injuryPercent = it.data.percent
                injuryRemain = it.data.endedAt

                EventBus.getDefault().post(SetInjuryAlarmEvent(injuryRemain!!))

                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            }, {
                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            }).disposeOnCleared()
    }

    fun getInjuryStatus() {
        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = true))

        repoV2.checkInjury()
            .subscribe({
                injuryStatus = when (it.data.status) {
                    InjuryStatus.NOT_STARTED.title -> InjuryStatus.NOT_STARTED
                    InjuryStatus.STARTED.title -> InjuryStatus.STARTED
                    else -> InjuryStatus.FINISHED
                }

                injuryPercent = it.data.percent
                injuryRemain = it.data.endedAt

                injuryRemain?.let {
                    val remain = injuryRemain ?: 0
                    if (remain > 0) {
                        EventBus.getDefault().post(SetInjuryAlarmEvent(injuryRemain!!))
                    }
                }


                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            }, {
                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            }).disposeOnCleared()
    }

    fun updateUser(
        setupUser: Boolean = false
    ) {

//        repoV2.editBodygraph(
//
//        )
        if (setupUser) setupCurrentUser()
    }

    fun loadFaqs() =
        repoV2.getFaq().subscribe({ faqsList.addAll(it.data) }, {}).disposeOnCleared()

    var reverseSuggestions: MutableLiveData<Pair<List<GeocodingNominatimFeature>, Int>> =
        mutableLiveDataOf(Pair(emptyList(), 0))
    var bodygraphPlace: MutableLiveData<List<GeocodingNominatimFeature>> =
        mutableLiveDataOf(emptyList())

    private fun getBodygraphPlace(lat: String, lon: String) {
        val acceptLang = if (App.preferences.locale == "es") "en" else App.preferences.locale

        repo.geocodingNominatim(
            "https://nominatim.openstreetmap.org/reverse?lat="
                    + lat + "&lon=" + lon
                    + "&format=json&accept-language=" + acceptLang + "&limit=50"
        ).subscribe({
            bodygraphPlace.postValue(it)
        }, {}).disposeOnCleared()
    }

    fun reverseNominatim(lat: String, lon: String, type: Int = 0) {
        val acceptLang = if (App.preferences.locale == "es") "en" else App.preferences.locale
        repo.geocodingNominatim(
            "https://nominatim.openstreetmap.org/reverse?lat="
                    + lat + "&lon=" + lon
                    + "&format=json&accept-language=" + acceptLang + "&limit=50"
        ).subscribe({
            reverseSuggestions.postValue(Pair(it, type))
        }, {
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            EventBus.getDefault().post(NoInetEvent())
        }).disposeOnCleared()
    }

    override fun onCleared() {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)

        super.onCleared()
    }

    @Subscribe
    fun onEvent(event: Any) { /* Dummy event subscription to prevent exceptions */
    }

    enum class InjuryStatus(val title: String) {
        NOT_STARTED("NOT_STARTED"),
        STARTED("STARTED"),
        FINISHED("FINISHED")
    }
}