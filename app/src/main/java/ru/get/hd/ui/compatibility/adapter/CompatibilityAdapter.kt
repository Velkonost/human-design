package ru.get.hd.ui.compatibility.adapter

import android.content.res.ColorStateList
import android.view.View
import androidx.core.content.ContextCompat
import com.airbnb.epoxy.EpoxyAdapter
import com.airbnb.epoxy.EpoxyModel
import kotlinx.android.synthetic.main.item_center.view.*
import kotlinx.android.synthetic.main.item_compatibility_children.view.*
import kotlinx.android.synthetic.main.item_compatibility_partners.view.*
import org.greenrobot.eventbus.EventBus
import ru.get.hd.App
import ru.get.hd.R
import ru.get.hd.event.DeleteChildEvent
import ru.get.hd.event.DeletePartnerEvent
import ru.get.hd.model.Center
import ru.get.hd.model.Child
import ru.get.hd.model.User
import ru.get.hd.util.ext.setUpRemoveItemTouchHelper

class CompatibilityAdapter : EpoxyAdapter() {

    fun createList(
        partners: List<User>,
        children: List<Child>
    ) {
        removeAllModels()

        addModel(PartnersModel(partners))
        addModel(ChildrenModel(children))

        notifyDataSetChanged()
    }
}

class PartnersModel(
    private val partners: List<User>
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view


        with(view) {
            val partnersAdapter = PartnersAdapter()
            partnersRecycler.adapter = partnersAdapter

            partnersRecycler.setUpRemoveItemTouchHelper { viewHolder, swipeDir ->
                val swipedPosition = viewHolder.absoluteAdapterPosition
                EventBus.getDefault().post(DeletePartnerEvent(partnersAdapter.getPartnerAtPosition(swipedPosition).id))

                partnersAdapter.deletePartner(swipedPosition)
            }

            partnersAdapter.createList(partners)
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_compatibility_partners
}

class ChildrenModel(
    private val children: List<Child>
) : EpoxyModel<View>() {

    private var root: View? = null

    override fun bind(view: View) {
        super.bind(view)
        root = view

        with(view) {
            val childrenAdapter = ChildrenAdapter()
            childrenRecycler.adapter = childrenAdapter

            childrenRecycler.setUpRemoveItemTouchHelper { viewHolder, swipeDir ->
                val swipedPosition = viewHolder.absoluteAdapterPosition
                EventBus.getDefault().post(DeleteChildEvent(childrenAdapter.getChildAtPosition(swipedPosition).id))

                childrenAdapter.deleteChild(swipedPosition)
            }

            childrenAdapter.createList(children)
        }
    }

    override fun getDefaultLayout(): Int = R.layout.item_compatibility_children
}