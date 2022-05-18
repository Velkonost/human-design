package ru.get.hd.ui.loader

import ru.get.hd.util.RxViewModel
import ru.get.hd.util.SingleLiveEvent
import javax.inject.Inject

class LoaderViewModel @Inject constructor(
) : RxViewModel() {

    val errorEvent = SingleLiveEvent<Error>()
}