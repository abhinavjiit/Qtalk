package com.example.qtalk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.qtalk.model.ContactsInfo
import kotlinx.android.synthetic.main.search_contact_adapter.view.*

class SearchContactAdapter(val clickListner: ClickListner) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var contactInfo: ArrayList<ContactsInfo>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.search_contact_adapter, parent, false)
        return ContactViewHolder(view)

    }

    override fun getItemCount(): Int {
        return if (contactInfo == null) 0 else contactInfo?.size!!

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        if (holder is ContactViewHolder) {
            holder.UserName.text = contactInfo?.get(position)?.displayName

            holder.UserName.setOnClickListener {
                clickListner.onRecyclerClick(position)
            }
        }
    }


    fun setData(list: ArrayList<ContactsInfo>?) {
        contactInfo = list
    }

    class ContactViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val UserName: TextView = mView.UserName
    }


    interface ClickListner {
        fun onRecyclerClick(position: Int)
    }

}