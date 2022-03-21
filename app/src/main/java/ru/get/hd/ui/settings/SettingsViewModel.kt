package ru.get.hd.ui.settings

import ru.get.hd.util.RxViewModel
import ru.get.hd.util.SingleLiveEvent
import javax.inject.Inject

class SettingsViewModel @Inject constructor(
) : RxViewModel() {

    val errorEvent = SingleLiveEvent<Error>()
}