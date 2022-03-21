package ru.get.hd.vm

import android.util.Log
import com.google.firebase.database.core.Repo
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import ru.get.hd.App
import ru.get.hd.model.Faq
import ru.get.hd.model.User
import ru.get.hd.repo.AppDatabase
import ru.get.hd.repo.base.RestRepo
import ru.get.hd.util.RxViewModel
import ru.get.hd.util.SingleLiveEvent
import javax.inject.Inject

class BaseViewModel @Inject constructor(
    private val repo: RestRepo
) : RxViewModel() {

    val errorEvent = SingleLiveEvent<String>()
    val successEvent = SingleLiveEvent<String>()
    val setupNavMenuEvent = SingleLiveEvent<String>()

    lateinit var currentUser: User
    val faqsList: MutableList<Faq> = mutableListOf()

    init {
        if (EventBus.getDefault().isRegistered(this))
        EventBus.getDefault().register(this)
    }

    fun createNewUser(
        name: String,
        place: String,
        date: String,
        time: String,
    ) {

        GlobalScope.launch {
            val userId = System.currentTimeMillis().toString()

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
                        forecastWeekMills = System.currentTimeMillis() / 604800000
                    )
                )

            App.preferences.currentUserId = userId
            setupCurrentUser()
        }

    }

    fun setupCurrentUser() {
        GlobalScope.launch {
            currentUser = App.database.userDao()
                .findById(App.preferences.currentUserId!!)

        }
    }

    fun deleteUser(
        userIdToDelete: String
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