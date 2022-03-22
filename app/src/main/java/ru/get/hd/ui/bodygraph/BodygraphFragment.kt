package ru.get.hd.ui.bodygraph

import android.os.Bundle
import android.util.Log
import org.greenrobot.eventbus.EventBus
import ru.get.hd.databinding.FragmentBodygraphBinding
import ru.get.hd.databinding.FragmentFaqBinding
import ru.get.hd.event.SetupNavMenuEvent
import ru.get.hd.ui.base.BaseFragment
import ru.get.hd.ui.faq.FaqFragment
import ru.get.hd.ui.faq.FaqViewModel

class BodygraphFragment : BaseFragment<BodygraphViewModel, FragmentBodygraphBinding>(
    ru.get.hd.R.layout.fragment_bodygraph,
    BodygraphViewModel::class,
    Handler::class
) {

    override fun onLayoutReady(savedInstanceState: Bundle?) {
        super.onLayoutReady(savedInstanceState)

        Log.d("keke", "ekke")
        EventBus.getDefault().post(SetupNavMenuEvent())
    }

    inner class Handler {}

}