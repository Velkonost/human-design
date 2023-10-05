package com.myhumandesignhd.vm

import androidx.lifecycle.MutableLiveData
import com.myhumandesignhd.App
import com.myhumandesignhd.event.CurrentUserLoadedEvent
import com.myhumandesignhd.event.HelpType
import com.myhumandesignhd.event.NoInetEvent
import com.myhumandesignhd.event.SetupNotificationsEvent
import com.myhumandesignhd.event.ShowHelpEvent
import com.myhumandesignhd.event.UpdateLoaderStateEvent
import com.myhumandesignhd.model.Affirmation
import com.myhumandesignhd.model.Child
import com.myhumandesignhd.model.CompatibilityResponse
import com.myhumandesignhd.model.Cycle
import com.myhumandesignhd.model.DailyAdvice
import com.myhumandesignhd.model.DesignChildResponse
import com.myhumandesignhd.model.Faq
import com.myhumandesignhd.model.Forecast
import com.myhumandesignhd.model.GeocodingNominatimFeature
import com.myhumandesignhd.model.GetDesignResponse
import com.myhumandesignhd.model.TransitResponse
import com.myhumandesignhd.model.User
import com.myhumandesignhd.model.getDateStr
import com.myhumandesignhd.repo.base.RestRepo
import com.myhumandesignhd.util.RxViewModel
import com.myhumandesignhd.util.SingleLiveEvent
import com.myhumandesignhd.util.ext.mutableLiveDataOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject


class BaseViewModel @Inject constructor(
    private val repo: RestRepo
) : RxViewModel() {

    val errorEvent = SingleLiveEvent<String>()
    val successEvent = SingleLiveEvent<String>()
    val setupNavMenuEvent = SingleLiveEvent<String>()

    val currentUserSetupEvent = SingleLiveEvent<Boolean>()

    fun isCurrentUserInitialized() = ::currentUser.isInitialized
    lateinit var currentUser: User

    var allUsers: MutableLiveData<List<User>> = mutableLiveDataOf(emptyList())

    var currentCompatibility: MutableLiveData<CompatibilityResponse> = mutableLiveDataOf(
        CompatibilityResponse()
    )
    var currentBodygraph: MutableLiveData<GetDesignResponse> = mutableLiveDataOf(GetDesignResponse())
    var currentPartnerBodygraph: MutableLiveData<GetDesignResponse> = mutableLiveDataOf(
        GetDesignResponse()
    )
    var currentChildBodygraph: MutableLiveData<DesignChildResponse> = mutableLiveDataOf(
        DesignChildResponse()
    )
    var currentAffirmation: MutableLiveData<Affirmation> = mutableLiveDataOf(Affirmation())
    var affirmations: MutableList<Affirmation> = mutableListOf()

    var currentForecast: MutableLiveData<Forecast> = mutableLiveDataOf(Forecast())
    var currentDailyAdvice: MutableLiveData<DailyAdvice> = mutableLiveDataOf(DailyAdvice())
    var currentCycles: MutableLiveData<List<Cycle>> = mutableLiveDataOf(emptyList())

    var currentTransit: MutableLiveData<TransitResponse> = mutableLiveDataOf(TransitResponse())

    val faqsList: MutableList<Faq> = mutableListOf()

    val updateUsersEvent = SingleLiveEvent<Boolean>()

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

    fun setUserInfo(
        gclid: String,
        appInstanceId: String
    ) {
        repo.setUserInfo(
            "http://5.45.79.21/setuserinfo.php",
            gclid,
            appInstanceId
        )
            .subscribe({}, {}).disposeOnCleared()
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
        currentUser.pushForecastPosition ++
        updateUser()
    }

    private fun checkIsUserDataLoaded() {
        if (
            isUserLoaded
            && isBodygraphLoaded
            && isAffirmationLoaded
            && isForecastLoaded
            && isTransitLoaded
            && isDailyAdviceLoaded
            && isCyclesLoaded
        ) {
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            EventBus.getDefault().post(CurrentUserLoadedEvent())

            EventBus.getDefault().post(ShowHelpEvent(type = HelpType.BodygraphCenters))

            EventBus.getDefault().post(SetupNotificationsEvent())
        }
    }

    fun setupCompatibility(
        lat1: String,
        lon1: String,
        date: String,
        onComplete: () -> Unit
    ) {

        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = true))

        repo.getCompatibility(
            language = App.preferences.locale,
            lat = currentUser.lat,
            lon = currentUser.lon,
            date = currentUser.getDateStr(),
            lat1, lon1, date
        ).subscribe({
            onComplete.invoke()

            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            currentCompatibility.postValue(it)
        }, {
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            EventBus.getDefault().post(NoInetEvent())
        }).disposeOnCleared()
    }

    suspend fun updateBodygraphs() =
        coroutineScope {
            withContext(Dispatchers.IO) {
                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = true))
                var updatedAmount = 0
                val users = getAllUsers()

                if (users.isEmpty()) {
                    updateUsersEvent.postValue(true)
                }

                for (user in users) {
                    repo.getDesign(
                        language = App.preferences.locale,
                        lat = user.lat,
                        lon = user.lon,
                        date = user.getDateStr()
                    ).subscribe({
                        user.subtitle1Ru = it.typeRu
                        user.subtitle1En = it.typeEn

                        if (App.preferences.locale == "es")
                            user.subtitle1En = it.typeEs

                        user.subtitle2 = it.line

                        user.subtitle3Ru = it.profileRu
                        user.subtitle3En = it.profileEn

                        if (App.preferences.locale == "es")
                            user.subtitle3En = it.profileEs

                        updateUser(user)
                        updatedAmount ++

                        if (updatedAmount == users.size) {
                            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
                            updateUsersEvent.postValue(true)
                        }
                    }, {
                        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
                        EventBus.getDefault().post(NoInetEvent())
                    })
                }
            }
        }


    private fun setupCurrentBodygraph() {
        repo.getDesign(
            language = App.preferences.locale,
            lat = currentUser.lat,
            lon = currentUser.lon,
            date = currentUser.getDateStr()
        ).subscribe({
            currentUser.subtitle1Ru = it.typeRu
            currentUser.subtitle1En = it.typeEn

            if (App.preferences.locale == "es")
                currentUser.subtitle1En = it.typeEs

            currentUser.subtitle2 = it.line

            currentUser.subtitle3Ru = it.profileRu
            currentUser.subtitle3En = it.profileEn

            if (App.preferences.locale == "es")
                currentUser.subtitle3En = it.profileEs

            currentUser.parentDescription = it.parentDescription

            updateUser()

            currentBodygraph.postValue(it)

            isBodygraphLoaded = true
            checkIsUserDataLoaded()

            setupCurrentDailyAdvice()
            setupCurrentCycles()

        }, {
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            EventBus.getDefault().post(NoInetEvent())
        }).disposeOnCleared()
    }

    private fun setupCurrentTransit() {
        val formatter: DateFormat = SimpleDateFormat(App.DATE_FORMAT, Locale.getDefault())
        val calendar: Calendar = Calendar.getInstance()

        calendar.timeInMillis = System.currentTimeMillis()
        val currentDateStr = formatter.format(calendar.time)

        repo.getTransit(
            language = App.preferences.locale,
            lat = currentUser.lat,
            lon = currentUser.lon,
            date = currentUser.getDateStr(),
            currentDate = currentDateStr
        ).subscribe({
            currentTransit.postValue(it)

            isTransitLoaded = true
            checkIsUserDataLoaded()

        }, {
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            EventBus.getDefault().post(NoInetEvent())
        }).disposeOnCleared()
    }

    private fun setupCurrentForecast() {
        val currentWeek = System.currentTimeMillis() / 604800000

        if (currentUser.forecastWeekMills != currentWeek) {
            currentUser.forecastNumber ++
            currentUser.forecastWeekMills = currentWeek

            updateUser()
        }

        repo.getForecasts()
            .subscribe({
                var currentUserProfileId = 0

                if (!currentUser.subtitle1Ru.isNullOrEmpty()) {
                    currentUserProfileId =
                        when (currentUser.subtitle1Ru!!.lowercase()) {
                            "манифестор" -> 0
                            "генератор" -> 1
                            "манифестирующий генератор" -> 2
                            "проектор" -> 3
                            else -> 4
                        }
                } else if (!currentUser.subtitle1En.isNullOrEmpty()) {
                    currentUserProfileId =
                        when (currentUser.subtitle1En!!.lowercase()) {
                        "manifestor" -> 0
                        "generator" -> 1
                        "manifest generator" -> 2
                        "projector" -> 3
                        else -> 4
                    }
                }

                val position = currentUser.forecastNumber % it.size
                val forecast = it[currentUserProfileId.toString()]!![position]
                currentForecast.postValue(forecast)

                isForecastLoaded = true
                checkIsUserDataLoaded()

            }, {
                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
                EventBus.getDefault().post(NoInetEvent())
            }).disposeOnCleared()
    }

    private fun setupCurrentCycles() {
        repo.getCycles()
            .subscribe({
                val currentUserProfileId = when (currentUser.subtitle1Ru!!.lowercase(Locale.getDefault())) {
                    "манифестор" -> 0
                    "генератор" -> 1
                    "манифестирующий генератор" -> 2
                    "проектор" -> 3
                    else -> 4
                }

                currentCycles.postValue(it[currentUserProfileId.toString()])

                isCyclesLoaded = true
                checkIsUserDataLoaded()
            }, {
                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
                EventBus.getDefault().post(NoInetEvent())
            }).disposeOnCleared()
    }

    private fun setupCurrentDailyAdvice() {
        repo.getDailyAdvice()
            .subscribe({
                isDailyAdviceLoaded = true
                checkIsUserDataLoaded()
                val currentUserProfileId = when (currentUser.subtitle1Ru!!.lowercase(Locale.getDefault())) {
                    "манифестор" -> 0
                    "генератор" -> 1
                    "манифестирующий генератор" -> 2
                    "проектор" -> 3
                    else -> 4
                }

                val cal = Calendar.getInstance()
                val dayOfMonth = cal.get(Calendar.DAY_OF_MONTH) - 1

                currentDailyAdvice.postValue(it[currentUserProfileId.toString()]!![dayOfMonth % it[currentUserProfileId.toString()]!!.size])
            }, {
                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
                EventBus.getDefault().post(NoInetEvent())
            }).disposeOnCleared()

    }

    private fun setupCurrentAffirmation() {
        val today = System.currentTimeMillis() / 86400000

        if (currentUser.affirmationDayMills != today) {
            currentUser.affirmationNumber ++
            currentUser.affirmationDayMills = today

            updateUser()
        }

        repo.getAffirmations()
            .subscribe({
                affirmations.addAll(it)

                currentAffirmation.postValue(
                    it.find { affirmation ->
                        affirmation.id == currentUser.affirmationsIdsList!![currentUser.affirmationNumber % currentUser.affirmationsIdsList!!.size]
                    }
                )
//                currentAffirmation.postValue(it[currentUser.affirmationNumber % it.size])

                isAffirmationLoaded = true
                checkIsUserDataLoaded()

            }, {
            }).disposeOnCleared()
    }

    private fun setupPartnerBodygraph(
        partner: User
    ) {
        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = true))

        repo.getDesign(
            language = App.preferences.locale,
            lat = partner.lat,
            lon = partner.lon,
            date = partner.getDateStr()
        ).subscribe({
            currentPartnerBodygraph.postValue(it)
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))

            partner.subtitle1Ru = it.typeRu
            partner.subtitle1En = it.typeEn

            partner.subtitle2 = it.line

            partner.subtitle3Ru = it.profileRu
            partner.subtitle3En = it.profileEn

            partner.parentDescription = it.parentDescription

            GlobalScope.launch {
                App.database.userDao().updateUser(partner)
            }

        }, {
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            EventBus.getDefault().post(NoInetEvent())
        }).disposeOnCleared()
    }

    private fun setupChildBodygraph(
        child: Child
    ) {
        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = true))

        repo.getDesignChild(
            language = App.preferences.locale,
            lat = child.lat,
            lon = child.lon,
            date = child.getDateStr()
        ).subscribe({
            currentChildBodygraph.postValue(it)
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))

            child.subtitle1Ru = it.typeRu
            child.subtitle1En = it.typeEn

            if (App.preferences.locale == "es")
                child.subtitle1En = it.typeEs

            child.subtitle2 = it.line

            child.subtitle3Ru = it.profileRu
            child.subtitle3En = it.profileEn

            if (App.preferences.locale == "es")
                child.subtitle3En = it.profileEs

            child.kidDescriptionRu = it.kidDescriptionRu
            child.kidDescriptionEn = it.kidDescriptionEn

            if (App.preferences.locale == "es")
                child.kidDescriptionEn = it.kidDescriptionEs

            child.titles = it.childrenDescription.titles
            child.descriptions = it.childrenDescription.descriptions

            GlobalScope.launch {
                App.database.childDao().updateUser(child)
            }
        }, {
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            EventBus.getDefault().post(NoInetEvent())
        }).disposeOnCleared()
    }

    fun createNewUser(
        name: String,
        place: String,
        date: Long,
        time: String,
        lat: String,
        lon: String,
        fromCompatibility: Boolean = false
    ) {
        repo.getAffirmations()
            .subscribe({ affirmationsList ->
                val affirmationsIds = affirmationsList.map { it.id }.shuffled()
                val pushForecastIds = listOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9").shuffled()

                GlobalScope.launch {
                    val userId = System.currentTimeMillis()

                    val partner = User(
                        id = userId,
                        name = name.trim(),
                        place = place,
                        date = date,
                        time = time,
                        affirmationNumber = 0,
                        forecastNumber = (0..10).random(),
                        affirmationDayMills = System.currentTimeMillis() / 86400000,
                        forecastWeekMills = System.currentTimeMillis() / 604800000,
                        lat = lat,
                        lon = lon,
                        affirmationsIdsList = affirmationsIds,
                        pushForecastIdsList = pushForecastIds
                    )

                    App.database.userDao().insert(partner)

                    if (!fromCompatibility) {
                        App.preferences.currentUserId = userId
                        setupCurrentUser()
                    } else {
                        setupPartnerBodygraph(partner)
                    }
                }
            }, {

            }).disposeOnCleared()
    }

    fun createNewChild(
        name: String,
        place: String,
        date: Long,
        time: String,
        lat: String,
        lon: String
    ) {

        GlobalScope.launch {
            val childId = System.currentTimeMillis()

            val child = Child(
                id = childId,
                parentId = currentUser.id,
                name = name.trim(),
                place = place,
                date = date,
                time = time,
                affirmationNumber = 0,
                forecastNumber = 0,
                affirmationDayMills = System.currentTimeMillis() / 86400000,
                forecastWeekMills = System.currentTimeMillis() / 604800000,
                lat = lat,
                lon = lon
            )

            App.database.childDao().insert(child)
            setupChildBodygraph(child)
        }
    }

    suspend fun getAllUsers() =
        coroutineScope {
            withContext(Dispatchers.IO) {
                App.database.userDao().getAll()
            }
        }

    suspend fun getAllChildren() =
        coroutineScope {
            withContext(Dispatchers.IO) {
                App.database.childDao().getAll()
            }
        }

    fun setupCurrentUser() {
        GlobalScope.launch {
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = true))
            resetAllUserDataStates()

            currentUser = App.database.userDao()
                .findById(App.preferences.currentUserId)!!

            setupCurrentBodygraph()
            setupCurrentForecast()

            setupCurrentAffirmation()
            setupCurrentTransit()

            currentUserSetupEvent.postValue(true)

            isUserLoaded = true
            checkIsUserDataLoaded()
        }
    }

    fun deleteUser(
        userIdToDelete: Long,
        userIdNext: Long? = null
    ) {

        GlobalScope.launch {
            val userToDelete = App.database.userDao().findById(userIdToDelete)

            if (userToDelete != null) {

                App.database.userDao().delete(userToDelete)

                if (userIdToDelete == App.preferences.currentUserId) {
                    if (userIdNext != null) {
                        App.preferences.currentUserId = userIdNext
                    } else {
                        App.preferences.currentUserId = App.database.userDao().getAll().first().id
                    }

                    setupCurrentUser()
                }
            }
        }
    }

    fun deleteChild(
        childIdToDelete: Long
    ) {
        GlobalScope.launch {
            val childToDelete = App.database.childDao().findById(childIdToDelete)
            if (childToDelete != null) {
                App.database.childDao().delete(childToDelete)
            }
        }
    }

    fun updateUser(
        setupUser: Boolean = false
    ) {
        GlobalScope.launch {
            App.database.userDao().updateUser(currentUser)
        }.invokeOnCompletion {
            if (setupUser) setupCurrentUser()
        }
    }

    private fun updateUser(user: User) {
        GlobalScope.launch {
            App.database.userDao().updateUser(user)
        }
    }

    fun loadFaqs() {
        repo.getFaq()
            .subscribe({
                faqsList.addAll(it)
            }, {

            }).disposeOnCleared()
    }

    var reverseSuggestions: MutableLiveData<List<GeocodingNominatimFeature>> = mutableLiveDataOf(emptyList())
    fun reverseNominatim(
        lat: String,
        lon: String
    ) {
        val acceptLang = if (App.preferences.locale == "es") "en" else App.preferences.locale
        repo.geocodingNominatim(
            "https://nominatim.openstreetmap.org/reverse?lat="
                    + lat + "&lon=" + lon
                    + "&format=json&accept-language="
                    + acceptLang
                    + "&limit=50"
        ).subscribe({
//                suggestions.postValue(it)
            reverseSuggestions.postValue(it)
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
    fun onEvent(event: Any) {
        //Dummy event subscription to prevent exceptions
    }
}