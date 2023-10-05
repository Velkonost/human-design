package com.myhumandesignhd.ui.compatibility.adapter

import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.myhumandesignhd.R
import com.myhumandesignhd.event.ChangeCompatibilityViewPagerUserInputEvent
import com.myhumandesignhd.event.DeleteChildEvent
import com.myhumandesignhd.event.DeleteChildItemEvent
import com.myhumandesignhd.event.DeletePartnerEvent
import com.myhumandesignhd.event.DeletePartnerItemEvent
import com.myhumandesignhd.model.Child
import com.myhumandesignhd.model.User
import io.sulek.ssml.SSMLLinearLayoutManager
import kotlinx.android.synthetic.main.item_compatibility_children.view.childrenRecycler
import kotlinx.android.synthetic.main.item_compatibility_partners.view.partnersRecycler
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class CompatibilityAdapter : EpoxyAdapter() {

    val partnersAdapter = PartnersAdapter()
    val childrenAdapter = ChildrenAdapter()


    lateinit var partnersModel: PartnersModel
    lateinit var childrenModel: ChildrenModel

    var isCreated = false

    fun createList(
        partners: List<User>,
        children: List<Child>
    ) {
        removeAllModels()

        partnersModel = PartnersModel(partners, partnersAdapter)
        childrenModel = ChildrenModel(children, childrenAdapter)

        addModel(partnersModel)
        addModel(childrenModel)

        isCreated = true

        notifyDataSetChanged()
    }

    fun updateList(
        partners: List<User>,
        children: List<Child>
    ) {
        partnersModel.partners = partners
        childrenModel.children = children

        partnersAdapter.createList(partners)
        childrenAdapter.createList(children)

    }
}

class PartnersModel(
    var partners: List<User>,
    val partnersAdapter: PartnersAdapter
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)

        with(view) {

            partnersRecycler.adapter = partnersAdapter

            (partnersRecycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            partnersRecycler.itemAnimator = null
            partnersRecycler.layoutManager = SSMLLinearLayoutManager(context)

            partnersRecycler.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                    when (e.action) {
                        MotionEvent.ACTION_UP -> EventBus.getDefault().post(
                            ChangeCompatibilityViewPagerUserInputEvent(true)
                        )
                        else -> EventBus.getDefault().post(
                            ChangeCompatibilityViewPagerUserInputEvent(false)
                        )
                    }
                }

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

                }
            })

            partnersAdapter.createList(partners)
        }
    }

    fun updatePartnersList(partners: List<User>) {
        this.partners = partners
    }

    @Subscribe
    fun onDeletePartnerItemEvent(e: DeletePartnerItemEvent) {
        EventBus.getDefault().post(DeletePartnerEvent(e.partnerId))
        partnersAdapter.deletePartnerById(e.partnerId)
    }

    override fun unbind(view: View) {
        super.unbind(view)

        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
    }

    override fun getDefaultLayout(): Int = R.layout.item_compatibility_partners
}

class ChildrenModel(
    var children: List<Child>,
    private val childrenAdapter: ChildrenAdapter
) : EpoxyModel<View>() {

    private var root: View? = null



    override fun preBind(view: View, previouslyBoundModel: EpoxyModel<*>?) {
        super.preBind(view, previouslyBoundModel)

        with(view) {
            (childrenRecycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            childrenRecycler.itemAnimator = null
            childrenRecycler.layoutManager = SSMLLinearLayoutManager(context)
        }
    }

    override fun bind(view: View) {
        super.bind(view)
        root = view

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)

        with(view) {



            childrenRecycler.adapter = childrenAdapter

            childrenAdapter.createList(children)
        }
    }

    @Subscribe
    fun onDeleteChildItemEvent(e: DeleteChildItemEvent) {
        EventBus.getDefault().post(DeleteChildEvent(e.childId))
        childrenAdapter.deleteChildById(e.childId)
    }

    override fun unbind(view: View) {
        super.unbind(view)

        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
    }


    override fun getDefaultLayout(): Int = R.layout.item_compatibility_children
}