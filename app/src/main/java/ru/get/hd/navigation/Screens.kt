package ru.get.hd.navigation

import android.window.SplashScreen
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.get.hd.ui.adduser.AddUserFragment
import ru.get.hd.ui.affirmation.AffirmationFragment
import ru.get.hd.ui.bodygraph.BodygraphFragment
import ru.get.hd.ui.bodygraph.diagram.DiagramFragment
import ru.get.hd.ui.bodygraph.first.BodygraphFirstFragment
import ru.get.hd.ui.bodygraph.second.BodygraphSecondFragment
import ru.get.hd.ui.compatibility.CompatibilityFragment
import ru.get.hd.ui.faq.FaqFragment
import ru.get.hd.ui.faq.detail.FaqDetailFragment
import ru.get.hd.ui.settings.SettingsFragment
import ru.get.hd.ui.settings.personal.PersonalInfoFragment
import ru.get.hd.ui.splash.SplashFragment
import ru.get.hd.ui.start.StartFragment
import ru.get.hd.ui.transit.TransitFragment

object Screens {

    fun splashScreen() = FragmentScreen {
        SplashFragment()
    }

    fun startScreen() = FragmentScreen {
        StartFragment()
    }

    fun bodygraphScreen() = FragmentScreen {
        BodygraphFragment.invoke()
    }

    fun transitScreen() = FragmentScreen {
        TransitFragment.invoke()
    }

    fun settingsScreen() = FragmentScreen {
        SettingsFragment()
    }

    fun compatibilityScreen() = FragmentScreen {
        CompatibilityFragment()
    }

    fun affirmationScreen() = FragmentScreen {
        AffirmationFragment()
    }

    fun bodygraphFirstScreen() = BaseFragmentScreen (
        fragment = BodygraphFirstFragment.invoke(),
        key = "bodygraph_first"
    )

    fun bodygraphSecondScreen() = BaseFragmentScreen (
        fragment = BodygraphSecondFragment.invoke(),
        key = "bodygraph_second"
    )

    fun faqScreen() = FragmentScreen {
        FaqFragment()
    }

    fun faqDetailScreen(
        title: String,
        desc: String
    ) = FragmentScreen {
        val faqDetailFragment = FaqDetailFragment()
        faqDetailFragment.arguments = bundleOf(
            "title" to title,
            "desc" to desc
        )

        faqDetailFragment
    }

    fun diagramScreen() = FragmentScreen {
        DiagramFragment()
    }

    fun addUserScreen() = FragmentScreen {
        AddUserFragment()
    }

    fun personalInfoScreen() = FragmentScreen {
        PersonalInfoFragment()
    }
}

class BaseFragmentScreen(
    private val fragment: Fragment,
    private val isClear: Boolean = false,
    private val key: String
) :
    FragmentScreen {
    override fun createFragment(factory: FragmentFactory): Fragment = fragment

    fun toFragment() = fragment

    override val clearContainer: Boolean = isClear
    override val screenKey: String = key
}