package com.myhumandesignhd.ui.compatibility.adapter

import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import com.myhumandesignhd.App
import com.myhumandesignhd.R
import com.myhumandesignhd.event.AddChildClickEvent
import com.myhumandesignhd.event.AddPartnerClickEvent
import com.myhumandesignhd.event.ChangeCompatibilityViewPagerUserInputEvent
import com.myhumandesignhd.event.DeleteChildEvent
import com.myhumandesignhd.event.DeleteChildItemEvent
import com.myhumandesignhd.event.DeletePartnerEvent
import com.myhumandesignhd.event.DeletePartnerItemEvent
import com.myhumandesignhd.model.response.BodygraphResponse
import io.sulek.ssml.SSMLLinearLayoutManager
import kotlinx.android.synthetic.main.item_compatibility_children.view.addChildBtn
import kotlinx.android.synthetic.main.item_compatibility_children.view.childrenRecycler
import kotlinx.android.synthetic.main.item_compatibility_children.view.emptyBlockChild
import kotlinx.android.synthetic.main.item_compatibility_children.view.emptyIconChild
import kotlinx.android.synthetic.main.item_compatibility_children.view.emptyTextChild
import kotlinx.android.synthetic.main.item_compatibility_children.view.emptyTitleChild
import kotlinx.android.synthetic.main.item_compatibility_partners.view.addPartnerBtn
import kotlinx.android.synthetic.main.item_compatibility_partners.view.emptyAnim
import kotlinx.android.synthetic.main.item_compatibility_partners.view.emptyBlock
import kotlinx.android.synthetic.main.item_compatibility_partners.view.emptyText
import kotlinx.android.synthetic.main.item_compatibility_partners.view.emptyTitle
import kotlinx.android.synthetic.main.item_compatibility_partners.view.partnersRecycler
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class CompatibilityAdapter : EpoxyAdapter() {

    val partnersAdapter = PartnersAdapter()
    val childrenAdapter = ChildrenAdapter()

    lateinit var partnersModel: PartnersModel
    lateinit var childrenModel: ChildrenModel

    private var childrenItems: MutableList<BodygraphResponse> = mutableListOf()
    private var partnersItems: MutableList<BodygraphResponse> = mutableListOf()

    var isCreated = false

    fun createList(
        partners: List<BodygraphResponse>,
        children: List<BodygraphResponse>,
        forceRecreate: Boolean = false

    ) {
        childrenItems = children.toMutableList()
        partnersItems = partners.toMutableList()

        if (!isCreated || forceRecreate) {
            removeAllModels()

            if (!partnersAdapter.hasObservers()) {
                partnersAdapter.setHasStableIds(true)
            }
            if (!childrenAdapter.hasObservers()) {
                childrenAdapter.setHasStableIds(true)
            }

            partnersModel = PartnersModel(partners, partnersAdapter)
            childrenModel = ChildrenModel(children, childrenAdapter)

            addModel(partnersModel)
            addModel(childrenModel)
        } else {
            partnersModel.partners = partners
            childrenModel.children = children

            partnersAdapter.createList(partners)
            childrenAdapter.createList(children)
        }

        notifyDataSetChanged()
        isCreated = true

    }

    fun updateList(partners: List<BodygraphResponse>, children: List<BodygraphResponse>) {
        partnersModel.partners = partners
        childrenModel.children = children
//        notifyDataSetChanged()

        partnersAdapter.createList(partners)
        childrenAdapter.createList(children)
    }
}

class PartnersModel(
    var partners: List<BodygraphResponse>,
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

            kotlin.runCatching {
                (partnersRecycler.itemAnimator as SimpleItemAnimator)
                    .supportsChangeAnimations = false
            }
            kotlin.runCatching { partnersRecycler.itemAnimator = null }
            kotlin.runCatching { partnersRecycler.layoutManager = SSMLLinearLayoutManager(context) }

            val animator: RecyclerView.ItemAnimator? = partnersRecycler.itemAnimator
            if (animator is SimpleItemAnimator) {
                animator.supportsChangeAnimations = false
            }

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

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            })

            partnersAdapter.createList(partners)

            if (partners.isNullOrEmpty()) {
                emptyAnim.setAnimation(
                    if (App.preferences.isDarkTheme) R.raw.compatibility_empty_light
                    else R.raw.compatibility_empty_dark
                )
                emptyAnim.playAnimation()

                emptyBlock.isVisible = true
                addPartnerBtn.isVisible = true
            } else {
                emptyBlock.isVisible = false
                addPartnerBtn.isVisible = false
            }


            emptyTitle.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
            emptyText.setTextColor(
                ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            addPartnerBtn.setOnClickListener {
                EventBus.getDefault().post(AddPartnerClickEvent())
            }
        }
    }

    fun updatePartnersList(partners: List<BodygraphResponse>) {
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
    var children: List<BodygraphResponse>,
    private val childrenAdapter: ChildrenAdapter
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun preBind(view: View, previouslyBoundModel: EpoxyModel<*>?) {
        super.preBind(view, previouslyBoundModel)

        with(view) {
            kotlin.runCatching {
                (childrenRecycler.itemAnimator as SimpleItemAnimator).supportsChangeAnimations =
                    false
            }
            kotlin.runCatching { childrenRecycler.itemAnimator = null }
            kotlin.runCatching { childrenRecycler.layoutManager = SSMLLinearLayoutManager(context) }

        }
    }

    override fun bind(view: View) {
        super.bind(view)
        root = view

        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)

        with(view) {
            kotlin.runCatching { childrenRecycler.itemAnimator = null }

            val animator: RecyclerView.ItemAnimator? = childrenRecycler.itemAnimator
            if (animator is SimpleItemAnimator) {
                animator.supportsChangeAnimations = false
            }

            childrenRecycler.adapter = childrenAdapter

            childrenAdapter.createList(children)

            emptyIconChild.setImageResource(
                if (App.preferences.isDarkTheme) R.drawable.ic_compatibility_child_light
                else R.drawable.ic_compatiblity_child_empty_dark
            )

            emptyBlockChild.isVisible = children.isEmpty()
            addChildBtn.isVisible = children.isEmpty()

            emptyTitleChild.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))
            emptyTextChild.setTextColor(ContextCompat.getColor(
                context,
                if (App.preferences.isDarkTheme) R.color.lightColor
                else R.color.darkColor
            ))

            addChildBtn.setOnClickListener {
                EventBus.getDefault().post(AddChildClickEvent())
            }

            childrenRecycler.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
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

                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            })
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