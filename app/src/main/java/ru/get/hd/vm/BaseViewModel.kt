package ru.get.hd.vm

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.hd.App
import ru.get.hd.model.Affirmation
import ru.get.hd.model.Faq
import ru.get.hd.model.Forecast
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

    lateinit var currentUser: User
    var currentAffirmation: MutableLiveData<Affirmation> = mutableLiveDataOf(Affirmation())
    var currentForecast: MutableLiveData<Forecast> = mutableLiveDataOf(Forecast())
    var currentTransit: MutableLiveData<TransitResponse> = mutableLiveDataOf(TransitResponse())

    val faqsList: MutableList<Faq> = mutableListOf()

    init {
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
    }

    fun setupCurrentTransit() {
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
            Log.d("keke", "lele")
        }, {

        }).disposeOnCleared()
    }

    fun setupCurrentForecast() {
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
            }, {

            }).disposeOnCleared()
    }

    fun setupCurrentAffirmation() {
        val today = System.currentTimeMillis() / 86400000

        if (currentUser.affirmationDayMills != today) {
            currentUser.affirmationNumber ++
            currentUser.affirmationDayMills = today

            updateUser()
        }

        repo.getAffirmations()
            .subscribe({
                currentAffirmation.postValue(it[currentUser.affirmationNumber % it.size])
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

            Log.d("keke", "1")
            App.preferences.currentUserId = userId
            setupCurrentUser()
        }

    }

    fun setupCurrentUser() {
        GlobalScope.launch {
            currentUser = App.database.userDao()
                .findById(App.preferences.currentUserId)

            setupCurrentForecast()
            setupCurrentAffirmation()
            setupCurrentTransit()
            Log.d("keke", "2")
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