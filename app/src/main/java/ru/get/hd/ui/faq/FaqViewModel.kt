package ru.get.hd.ui.faq

import ru.get.hd.util.RxViewModel
import ru.get.hd.util.SingleLiveEvent
import javax.inject.Inject

class FaqViewModel @Inject constructor(
) : RxViewModel() {

    val errorEvent = SingleLiveEvent<Error>()
}