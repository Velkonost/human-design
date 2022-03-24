package ru.get.hd.ui.bodygraph.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.FragmentStatePagerAdapter
import ru.get.hd.App
import ru.get.hd.navigation.Screens
import ru.get.hd.ui.bodygraph.first.BodygraphFirstFragment
import ru.get.hd.ui.bodygraph.second.BodygraphSecondFragment

class VerticalViewPagerAdapter(
    fragmentManager: FragmentManager
) : FragmentStatePagerAdapter(fragmentManager) {
    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment =
        when(position) {
            0 -> Screens.bodygraphFirstScreen().toFragment()
            else -> Screens.bodygraphSecondScreen().toFragment()
        }



}