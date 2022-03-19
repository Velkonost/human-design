package ru.get.hd.ui.start

import ru.get.hd.util.RxViewModel
import ru.get.hd.util.SingleLiveEvent
import javax.inject.Inject

class StartViewModel @Inject constructor(
) : RxViewModel() {

    val errorEvent = SingleLiveEvent<Error>()
}