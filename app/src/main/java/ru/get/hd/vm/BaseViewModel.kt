package ru.get.hd.vm

import android.util.Log
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
import ru.get.hd.event.UpdateLoaderStateEvent
import ru.get.hd.model.Affirmation
import ru.get.hd.model.Faq
import ru.get.hd.model.Forecast
import ru.get.hd.model.GetDesignResponse
import ru.get.hd.model.TransitResponse
import ru.get.hd.model.User
import ru.get.hd.repo.base.RestRepo
import ru.get.hd.util.RxViewModel
import ru.get.hd.util.SingleLiveEvent
import ru.get.hd.util.ext.mutableLiveDataOf
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
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

    var currentBodygraph: MutableLiveData<GetDesignResponse> = mutableLiveDataOf(GetDesignResponse())
    var currentAffirmation: MutableLiveData<Affirmation> = mutableLiveDataOf(Affirmation())
    var currentForecast: MutableLiveData<Forecast> = mutableLiveDataOf(Forecast())
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

    private fun resetAllUserDataStates() {
        isUserLoaded = false
        isBodygraphLoaded = false
        isAffirmationLoaded = false
        isForecastLoaded = false
        isTransitLoaded = false
    }

    private fun checkIsUserDataLoaded() {
        if (
            isUserLoaded
            && isBodygraphLoaded
            && isAffirmationLoaded
            && isForecastLoaded
            && isTransitLoaded
        ) {
            EventBus.getDefault().post(UpdateLoaderStateEvent(isVisible = false))
            EventBus.getDefault().post(CurrentUserLoadedEvent())
        }
    }

    private fun setupCurrentBodygraph() {
        val formatter: DateFormat = SimpleDateFormat(App.DATE_FORMAT, Locale.getDefault())
        val calendar: Calendar = Calendar.getInstance()

        calendar.timeInMillis = currentUser.date
        val dateStr = formatter.format(calendar.time)

        repo.getDesign(
            language = App.preferences.locale,
            lat = currentUser.lat,
            lon = currentUser.lon,
            date = dateStr
        ).subscribe({
            currentUser.subtitle1Ru = it.typeRu
            currentUser.subtitle1En = it.typeEn

            currentUser.subtitle2 = it.line

            currentUser.subtitle3Ru = it.profileRu
            currentUser.subtitle3En = it.profileEn

            updateUser()

            currentBodygraph.postValue(it)

            isBodygraphLoaded = true
            checkIsUserDataLoaded()

        }, {}).disposeOnCleared()
    }

    private fun setupCurrentTransit() {
        val formatter: DateFormat = SimpleDateFormat(App.DATE_FORMAT, Locale.getDefault())
        val calendar: Calendar = Calendar.getInstance()

        calendar.timeInMillis = currentUser.date
        val dateStr = formatter.format(calendar.time)

        calendar.timeInMillis = System.currentTimeMillis()
        val currentDateStr = formatter.format(calendar.time)

        repo.getTransit(
            language = App.preferences.locale,
            lat = currentUser.lat,
            lon = currentUser.lon,
            date = dateStr,
            currentDate = currentDateStr
        ).subscribe({
            currentTransit.postValue(it)

            isTransitLoaded = true
            checkIsUserDataLoaded()

        }, {

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

            }, {}).disposeOnCleared()
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

    fun createNewUser(
        name: String,
        place: String,
        date: Long,
        time: String,
        lat: String,
        lon: String
    ) {

        GlobalScope.launch {
            val userId = System.currentTimeMillis()

            App.database.userDao()
                .insert(
                    User(
                        id = userId,
                        name = name,
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
                )

            App.preferences.currentUserId = userId
            setupCurrentUser()
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
                currentUser = App.database.userDao().getAll().first()
            }
        }
    }

    private fun updateUser() {
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