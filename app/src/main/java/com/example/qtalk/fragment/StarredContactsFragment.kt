package com.example.qtalk.fragment

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qtalk.AllContactsListAdapter
import com.example.qtalk.ContactDetailActivity
import com.example.qtalk.R
import com.example.qtalk.model.ContactsInfo
import com.facebook.shimmer.ShimmerFrameLayout

class StarredContactsFragment : Fragment(), AllContactsListAdapter.ClickListner {
    lateinit var adapter: AllContactsListAdapter
    private lateinit var listView: RecyclerView
    private var contactsInfoList: ArrayList<ContactsInfo>? = null
    private lateinit var shimmer: ShimmerFrameLayout
    private val PROJECTION: Array<out String> = arrayOf(
        ContactsContract.Data._ID,
        ContactsContract.Data.MIMETYPE,
        ContactsContract.Data.DATA1,
        ContactsContract.Data.DATA2,
        ContactsContract.Data.DATA3,
        ContactsContract.Data.DATA4,
        ContactsContract.Data.DATA5,
        ContactsContract.Data.DATA6,
        ContactsContract.Data.DATA7,
        ContactsContract.Data.DATA8,
        ContactsContract.Data.DATA9,
        ContactsContract.Data.DATA10,
        ContactsContract.Data.DATA11,
        ContactsContract.Data.DATA12,
        ContactsContract.Data.DATA13,
        ContactsContract.Data.DATA14,
        ContactsContract.Data.DATA15
    )

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
        getContacts()
        return view
    }

    private fun getContacts() {
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
                    /*  val emailcrusor: Cursor? = getContentResolver().query(
                          ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                         null,
                          ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?",
                          arrayOf<String?>(contactId),
                          null
                      )
                      if (emailcrusor?.moveToNext()!!) {
                          val email: String =
                          emailcrusor.getString(emailcrusor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                          contactsInfo.email = email
                          Log.d("Tag", email)
                      }*/


                    //  emailcrusor.close()
                    contactsInfoList?.add(contactsInfo)
                }
            }
        }
        cursor?.close()
        contactsInfoList?.let {
            adapter.setListData(it)
            adapter.notifyDataSetChanged()
        }

    }

    override fun onclick(position: Int) {
        val intent = Intent(activity, ContactDetailActivity::class.java)
        intent.putExtra("Contact_Id", contactsInfoList?.get(position)?.contactId)
        intent.putExtra("DisPlayName",contactsInfoList?.get(position)?.displayName)
        startActivity(intent)

    }
}