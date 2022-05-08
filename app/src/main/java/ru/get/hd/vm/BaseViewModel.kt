package ru.get.hd.vm

import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.hd.App
import ru.get.hd.event.CurrentUserLoadedEvent
import ru.get.hd.event.HelpType
import ru.get.hd.event.NoInetEvent
import ru.get.hd.event.ShowHelpEvent
import ru.get.hd.event.UpdateLoaderStateEvent
import ru.get.hd.model.Affirmation
import ru.get.hd.model.Child
import ru.get.hd.model.CompatibilityResponse
import ru.get.hd.model.DailyAdvice
import ru.get.hd.model.DesignChildResponse
import ru.get.hd.model.Faq
import ru.get.hd.model.Forecast
import ru.get.hd.model.GeocodingNominatimFeature
import ru.get.hd.model.GetDesignResponse
import ru.get.hd.model.TransitResponse
import ru.get.hd.model.User
import ru.get.hd.model.getDateStr
import ru.get.hd.repo.base.RestRepo
import ru.get.hd.util.RxViewModel
import ru.get.hd.util.SingleLiveEvent
import ru.get.hd.util.ext.mutableLiveDataOf
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashMap

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
    var currentPartnerBodygraph: MutableLiveData<GetDesignResponse> = mutableLiveDataOf(GetDesignResponse())
    var currentChildBodygraph: MutableLiveData<DesignChildResponse> = mutableLiveDataOf(DesignChildResponse())
    var currentAffirmation: MutableLiveData<Affirmation> = mutableLiveDataOf(Affirmation())

    var currentForecast: MutableLiveData<Forecast> = mutableLiveDataOf(Forecast())
    var currentDailyAdvice: MutableLiveData<DailyAdvice> = mutableLiveDataOf(DailyAdvice())

    var currentTransit: MutableLiveData<TransitResponse> = mutableLiveDataOf(TransitResponse())

    val faqsList: MutableList<Faq> = mutableListOf()

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

    private fun resetAllUserDataStates() {
        isUserLoaded = false
        isBodygraphLoaded = false
        isAffirmationLoaded = false
        isForecastLoaded = false
        isTransitLoaded = false
        isDailyAdviceLoaded = false
    }

    private fun checkIsUserDataLoaded() {
        if (
            isUserLoaded
            && isBodygraphLoaded
            && isAffirmationLoaded
            && isForecastLoaded
            && isTransitLoaded
            && isDailyAdviceLoaded
        ) {
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            EventBus.getDefault().post(CurrentUserLoadedEvent())

            EventBus.getDefault().post(ShowHelpEvent(type = HelpType.BodygraphCenters))
        }
    }

    fun setupCompatibility(
        lat1: String,
        lon1: String,
        date: Long,
        onComplete: () -> Unit
    ) {

        EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = true))

        val formatter: DateFormat = SimpleDateFormat(App.DATE_FORMAT, Locale.getDefault())
        val calendar: Calendar = Calendar.getInstance()

        calendar.timeInMillis = date
        val dateStr = formatter.format(calendar.time)

        repo.getCompatibility(
            language = App.preferences.locale,
            lat = currentUser.lat,
            lon = currentUser.lon,
            date = currentUser.getDateStr(),
            lat1, lon1, dateStr
        ).subscribe({
            onComplete.invoke()

            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            currentCompatibility.postValue(it)
        }, {
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            EventBus.getDefault().post(NoInetEvent())
        }).disposeOnCleared()
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

            currentUser.subtitle2 = it.line

            currentUser.subtitle3Ru = it.profileRu
            currentUser.subtitle3En = it.profileEn

            currentUser.parentDescription = it.parentDescription

            updateUser()

            currentBodygraph.postValue(it)

            isBodygraphLoaded = true
            checkIsUserDataLoaded()

            setupCurrentDailyAdvice()

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
                val allForecasts = mutableListOf<Forecast>()
                it.values.forEach { list-> allForecasts.plusAssign(list) }

                currentForecast.postValue(allForecasts[currentUser.forecastNumber % it.size])

                isForecastLoaded = true
                checkIsUserDataLoaded()

            }, {
                EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
                EventBus.getDefault().post(NoInetEvent())
            }).disposeOnCleared()
    }

    private fun setupCurrentDailyAdvice() {
        repo.getDailyAdvice()
            .subscribe({
                       Log.d("keke", "keke")
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

                currentDailyAdvice.postValue(it[currentUserProfileId.toString()]!![dayOfMonth])
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
                currentAffirmation.postValue(it[currentUser.affirmationNumber % it.size])

                isAffirmationLoaded = true
                checkIsUserDataLoaded()

            }, {}).disposeOnCleared()
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

            child.subtitle2 = it.line

            child.subtitle3Ru = it.profileRu
            child.subtitle3En = it.profileEn

            child.kidDescriptionRu = it.kidDescriptionRu
            child.kidDescriptionEn = it.kidDescriptionEn

            GlobalScope.launch {
                App.database.childDao().updateUser(child )
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

        GlobalScope.launch {
            val userId = System.currentTimeMillis()

            val partner = User(
                id = userId,
                name = name.replace(" ", ""),
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

            App.database.userDao().insert(partner)

            if (!fromCompatibility) {
                App.preferences.currentUserId = userId
                setupCurrentUser()
            } else {
                setupPartnerBodygraph(partner)
            }
        }

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
//            getSystemService(App.instance.applicationContext, ConnectivityManager::class.java) as ConnectivityManager
            val child = Child(
                id = childId,
                parentId = currentUser.id,
                name = name.replace(" ", ""),
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
                .findById(App.preferences.currentUserId)

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
        userIdToDelete: Long
    ) {

        GlobalScope.launch {
            val userToDelete = App.database.userDao().findById(userIdToDelete)
            App.database.userDao().delete(userToDelete)

            if (userIdToDelete == App.preferences.currentUserId) {
                App.preferences.currentUserId = App.database.userDao().getAll().first().id
                setupCurrentUser()
            }
        }
    }

    fun deleteChild(
        childIdToDelete: Long
    ) {
        GlobalScope.launch {
            val childToDelete = App.database.childDao().findById(childIdToDelete)
            App.database.childDao().delete(childToDelete)
        }
    }

    fun updateUser() {
        GlobalScope.launch {
            App.database.userDao().updateUser(currentUser)
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

        repo.geocodingNominatim(
            "https://nominatim.openstreetmap.org/reverse?lat="
                    + lat + "&lon=" + lon
                    + "&format=json&accept-language="
                    + App.preferences.locale
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