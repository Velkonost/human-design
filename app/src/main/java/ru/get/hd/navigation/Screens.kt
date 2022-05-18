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
import ru.get.hd.ui.compatibility.child.CompatibilityChildFragment
import ru.get.hd.ui.compatibility.detail.CompatibilityDetailFragment
import ru.get.hd.ui.compatibility.detail.info.CompatibilityDetailInfoFragment
import ru.get.hd.ui.faq.FaqFragment
import ru.get.hd.ui.faq.detail.FaqDetailFragment
import ru.get.hd.ui.loader.LoaderFragment
import ru.get.hd.ui.settings.SettingsFragment
import ru.get.hd.ui.settings.personal.PersonalInfoFragment
import ru.get.hd.ui.splash.SplashFragment
import ru.get.hd.ui.start.StartFragment
import ru.get.hd.ui.transit.TransitFragment

object Screens {

    fun loaderScreen() = FragmentScreen {
        LoaderFragment()
    }

    fun splashScreen() = FragmentScreen {
        SplashFragment()
    }

    fun startScreen() = FragmentScreen {
        StartFragment()
    }

    fun bodygraphScreen(
        fromStart: Boolean = false
    ) = FragmentScreen {
        val bodygraphFragment = BodygraphFragment()//.invoke()
        bodygraphFragment.arguments = bundleOf(
            "fromStart" to fromStart
        )

        bodygraphFragment
    }

    fun transitScreen() = FragmentScreen {
        TransitFragment.invoke()
    }

    fun settingsScreen() = FragmentScreen {
        SettingsFragment()
    }

    fun compatibilityScreen(
        fromAddChild: Boolean = false
    ) = FragmentScreen {
        val compatibilityScreen = CompatibilityFragment()
        compatibilityScreen.arguments = bundleOf(
            "fromAddChild" to fromAddChild
        )

        compatibilityScreen
    }

    fun affirmationScreen() = FragmentScreen {
        AffirmationFragment()
    }

    fun bodygraphFirstScreen() = BaseFragmentScreen (
        fragment = BodygraphFirstFragment.invoke(),
        key = "bodygraph_first"
    )

    fun bodygraphSecondScreen() = FragmentScreen {
        BodygraphSecondFragment.invoke()
    }

//        BaseFragmentScreen (
//        fragment = BodygraphSecondFragment.invoke(),
//        key = "bodygraph_second"
//    )

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

    fun addUserScreen(
        fromDiagram: Boolean = false,
        fromCompatibility: Boolean = false,
        isChild: Boolean = false
    ) = FragmentScreen {
        val addUserFragment = AddUserFragment()
        addUserFragment.arguments = bundleOf(
            "fromDiagram" to fromDiagram,
            "fromCompatibility" to fromCompatibility,
            "isChild" to isChild
        )

        addUserFragment
    }

    fun personalInfoScreen() = FragmentScreen {
        PersonalInfoFragment()
    }

    fun compatibilityDetailScreen(
        name: String,
        title: String,
        chartResId: Int
    ) = FragmentScreen {
        val compatibilityDetailFragment = CompatibilityDetailFragment()
        compatibilityDetailFragment.arguments = bundleOf(
            "name" to name,
            "title" to title,
            "chartResId" to chartResId
        )

        compatibilityDetailFragment
    }

    fun compatibilityChildScreen(
        childId: Long
    ) = FragmentScreen {
        val compatibilityChildFragment = CompatibilityChildFragment()
        compatibilityChildFragment.arguments = bundleOf(
            "childId" to childId
        )

        compatibilityChildFragment
    }

    fun compatibilityDetailInfoScreen() = FragmentScreen {
        CompatibilityDetailInfoFragment()
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