package com.example.qtalk

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qtalk.model.ContactsInfo
import kotlinx.android.synthetic.main.all_contacts_list_adapter_layout.view.*


class AllContactsListAdapter(context: Context, val clickListner: ClickListner) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var contactsInfoList: ArrayList<ContactsInfo>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.all_contacts_list_adapter_layout, parent, false)

        return ViewHolder(view)

    }

    fun setListData(list: ArrayList<ContactsInfo>) {
        this.contactsInfoList = list;
    }

    override fun getItemCount(): Int {
        return if (contactsInfoList == null)
            0
        else
            contactsInfoList!!.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ViewHolder) {
            holder.name.text = contactsInfoList?.get(position)?.displayName
            holder.number.text = contactsInfoList?.get(position)?.phoneNumber
            holder.root.setOnClickListener {
                clickListner.onclick(position)
            }

        }

    }

    class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {

        val name: TextView = mView.name
        val number: TextView = mView.number
        val root: RelativeLayout = mView.root
    }

    interface ClickListner {
        fun onclick(position: Int)
    }

}