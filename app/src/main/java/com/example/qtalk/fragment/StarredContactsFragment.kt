package com.example.qtalk.fragment

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qtalk.AllContactsListAdapter
import com.example.qtalk.ContactDetailActivity
import com.example.qtalk.R
import com.example.qtalk.model.ContactsInfo
import com.facebook.shimmer.ShimmerFrameLayout
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class StarredContactsFragment : Fragment(), AllContactsListAdapter.ClickListner {
    lateinit var adapter: AllContactsListAdapter
    private lateinit var listView: RecyclerView
    private var contactsInfoList: ArrayList<ContactsInfo>? = null
    private lateinit var shimmer: ShimmerFrameLayout


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.starred_contacts_fragment, container, false)

        listView = view.findViewById(R.id.listStarredOfContacts)
        contactsInfoList = ArrayList()
        activity?.let {
            adapter = AllContactsListAdapter(it, this)
            val llm = LinearLayoutManager(it)
            listView.layoutManager = llm
            listView.adapter = adapter
        }
        val starredData = Observable.fromCallable { getContacts() }

        starredData.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe(object : Observer<ArrayList<ContactsInfo>> {
                override fun onComplete() {
                }

                override fun onSubscribe(d: Disposable) {

                }

                override fun onNext(t: ArrayList<ContactsInfo>) {
                    if (t.size > 0) {
                        adapter.setListData(t)
                        adapter.notifyDataSetChanged()
                    } else {
                        val text = view.findViewById<TextView>(R.id.noContact)
                        text.visibility = View.VISIBLE
                    }
                }

                override fun onError(e: Throwable) {
                    Toast.makeText(activity, "something went wrong", Toast.LENGTH_SHORT).show()
                }

            })

        return view
    }

    private fun getContacts(): ArrayList<ContactsInfo> {
        val contentResolver = activity?.contentResolver
        var contactId: String? = null
        var displayName: String? = null
        val cursor: Cursor? = contentResolver?.query(
            ContactsContract.Contacts.CONTENT_URI,
            null,
            "starred=?",
            arrayOf("1"),
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
                    val phoneCursor: Cursor? = activity?.getContentResolver()?.query(
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
        return contactsInfoList!!
    }

    override fun onclick(position: Int) {
        val intent = Intent(activity, ContactDetailActivity::class.java)
        intent.putExtra("Contact_Id", contactsInfoList?.get(position)?.contactId)
        intent.putExtra("DisPlayName", contactsInfoList?.get(position)?.displayName)
        startActivity(intent)

    }
}