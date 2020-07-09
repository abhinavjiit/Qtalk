package com.example.qtalk.fragment

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.qtalk.AllContactsListAdapter
import com.example.qtalk.ContactDetailActivity
import com.example.qtalk.R
import com.example.qtalk.SearchContactActivty
import com.example.qtalk.model.ContactsInfo
import com.facebook.shimmer.ShimmerFrameLayout
import kotlinx.coroutines.*

class AllContactsFragment : Fragment(), AllContactsListAdapter.ClickListner, View.OnClickListener {
    lateinit var adapter: AllContactsListAdapter
    private lateinit var listView: RecyclerView
    private var contactsInfoList: ArrayList<ContactsInfo>? = null
    private lateinit var shimmer: ShimmerFrameLayout
    private lateinit var search: Button
    private lateinit var createNewContact: Button
    private lateinit var swipeRefresh: SwipeRefreshLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.all_contacts_fragment, container, false)
        listView = view.findViewById(R.id.listOfContacts)
        search = view.findViewById(R.id.search)
        createNewContact = view.findViewById(R.id.createNewContact)
        swipeRefresh = view.findViewById(R.id.swipeRefresh)
        contactsInfoList = ArrayList()
        activity?.let {
            adapter = AllContactsListAdapter(it, this)
            val llm = LinearLayoutManager(it)
            listView.layoutManager = llm
            listView.adapter = adapter
        }
        swipeRefresh.setOnRefreshListener {
            contactsInfoList?.clear()
            CoroutineScope(Dispatchers.IO).launch {
                getContacts()
            }
            swipeRefresh.isRefreshing = false

        }
        CoroutineScope(Dispatchers.IO).launch {
            getContacts()
        }


        search.setOnClickListener {
            val intent = Intent(activity, SearchContactActivty::class.java)
            intent.putParcelableArrayListExtra("ContactInfo", contactsInfoList)
            startActivity(intent)
        }
        createNewContact.setOnClickListener(this)
        return view
    }

    private fun getContacts() {
        val contentResolver = activity?.contentResolver
        var contactId: String? = null
        var displayName: String? = null
        val cursor: Cursor? = contentResolver?.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        if (cursor != null && cursor.count > 0) {

            while (cursor.moveToNext()) {
                val hasPhoneNumber: Int =
                    cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))
                        ?.toInt()!!
                if (hasPhoneNumber > 0) {
                    val contactsInfo = ContactsInfo()
                    contactId =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID))
                    displayName =
                        cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME))

                    contactsInfo.contactId = contactId
                    contactsInfo.displayName = displayName
                    val phoneCursor: Cursor? = activity?.contentResolver?.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf<String?>(contactId),
                        null
                    )
                    if (phoneCursor?.moveToNext()!!) {
                        val phoneNumber: String =
                            phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        contactsInfo.phoneNumber = phoneNumber

                    }
                    phoneCursor.close()

                    contactsInfoList?.add(contactsInfo)
                }
            }
        }
        cursor?.close()
        CoroutineScope(Dispatchers.Main).launch {
            contactsInfoList?.let {
                adapter.setListData(it)
                adapter.notifyDataSetChanged()
            }
        }


    }

    override fun onclick(position: Int) {
        val intent = Intent(activity, ContactDetailActivity::class.java)
        intent.putExtra("Contact_Id", contactsInfoList?.get(position)?.contactId)
        intent.putExtra("DisPlayName", contactsInfoList?.get(position)?.displayName)
        startActivity(intent)
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.createNewContact -> {
                val intent = Intent(
                    Intent.ACTION_INSERT,
                    ContactsContract.Contacts.CONTENT_URI
                )
                startActivity(intent)
            }


        }
    }

}