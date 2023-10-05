package com.myhumandesignhd.navigation

import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import com.myhumandesignhd.App
import com.myhumandesignhd.ui.adduser.AddUserFragment
import com.myhumandesignhd.ui.affirmation.AffirmationFragment
import com.myhumandesignhd.ui.bodygraph.BodygraphFragment
import com.myhumandesignhd.ui.bodygraph.diagram.DiagramFragment
import com.myhumandesignhd.ui.bodygraph.first.BodygraphFirstFragment
import com.myhumandesignhd.ui.bodygraph.second.BodygraphSecondFragment
import com.myhumandesignhd.ui.compatibility.CompatibilityFragment
import com.myhumandesignhd.ui.compatibility.child.CompatibilityChildFragment
import com.myhumandesignhd.ui.compatibility.detail.CompatibilityDetailFragment
import com.myhumandesignhd.ui.compatibility.detail.info.CompatibilityDetailInfoFragment
import com.myhumandesignhd.ui.faq.FaqFragment
import com.myhumandesignhd.ui.faq.detail.FaqDetailFragment
import com.myhumandesignhd.ui.loader.LoaderFragment
import com.myhumandesignhd.ui.paywall.PaywallFragment
import com.myhumandesignhd.ui.settings.SettingsFragment
import com.myhumandesignhd.ui.settings.personal.PersonalInfoFragment
import com.myhumandesignhd.ui.start.StartFragment
import com.myhumandesignhd.ui.transit.TransitFragment

object Screens {

    fun loaderScreen() = FragmentScreen {
        LoaderFragment()
    }

    fun paywallScreen(
        fromStart: Boolean = false,
        source: String
    ) = FragmentScreen {
        val paywallFragment = PaywallFragment()
        paywallFragment.arguments = bundleOf(
            "fromStart" to fromStart,
            "source" to source
        )
        paywallFragment
    }

    fun startScreen() = FragmentScreen {
        StartFragment()
    }

    fun bodygraphScreen(
        fromStart: Boolean = false,
        needUpdateNavMenu: Boolean = true
    ) = FragmentScreen {
        val bodygraphFragment = BodygraphFragment()//.invoke()
        bodygraphFragment.arguments = bundleOf(
            "fromStart" to fromStart,
            "needUpdateNavMenu" to needUpdateNavMenu
        )

        bodygraphFragment
    }

    fun transitScreen() = FragmentScreen {
        if (App.preferences.isInvokeNewTransits) {
            TransitFragment.reset()
            App.preferences.isInvokeNewTransits = false
        }
//        TransitFragment.invoke()
        TransitFragment()
    }

    fun settingsScreen() = FragmentScreen {
        SettingsFragment()
    }

    fun compatibilityScreen(
    ) = FragmentScreen {
        val compatibilityScreen = CompatibilityFragment()
        compatibilityScreen.arguments = bundleOf()

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
        if (App.preferences.isInvokeNewDescription) {
            BodygraphSecondFragment.reset()

            App.preferences.isInvokeNewDescription = false
        }
//        BodygraphSecondFragment.invoke()
        BodygraphSecondFragment()
    }

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