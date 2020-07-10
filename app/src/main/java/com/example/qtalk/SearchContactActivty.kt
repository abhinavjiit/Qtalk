package com.example.qtalk

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.qtalk.model.ContactsInfo

class SearchContactActivty : AppCompatActivity(), SearchContactAdapter.ClickListner {

    var contactInfo: ArrayList<ContactsInfo>? = null
    lateinit var adapter: SearchContactAdapter
    lateinit var searchRecyclerView: RecyclerView
    lateinit var search: EditText
    var list = ArrayList<ContactsInfo>()
    var pos = -1
    lateinit var nameList: ArrayList<ContactsInfo>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_contact_activity)
        searchRecyclerView = findViewById(R.id.searchRecyclerView)
        search = findViewById(R.id.search)
        nameList = ArrayList()
        contactInfo = intent.getParcelableArrayListExtra("ContactInfo")
        adapter = SearchContactAdapter(this)
        searchRecyclerView.layoutManager = LinearLayoutManager(this)
        searchRecyclerView.adapter = adapter
        contactInfo?.let {
            it.forEach { infoItem ->
                list.add(infoItem)
            }
            adapter.setData(list)
        }


        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(editable: Editable?) {
                filtered(editable.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })
    }

    private fun filtered(s: String) {
        if (s.isBlank()) {
            list.clear()
            contactInfo?.let {
                for (i in 0 until it.size)
                    list.add(it[i])
                adapter.setData(contactInfo)
                adapter.notifyDataSetChanged()
            }
        } else {
            contactInfo?.let {
                val localList = ArrayList<ContactsInfo>()
                for (i in 0 until it.size) {
                    if (it[i].displayName?.toLowerCase()?.contains(s)!!) {
                        val contactInfoData = ContactsInfo()
                        contactInfoData.displayName = it[i].displayName
                        contactInfoData.phoneNumber = it[i].phoneNumber
                        localList.add(contactInfoData)
                    }
                }
                list.clear()
                list = localList
                adapter.setData(list)
                adapter.notifyDataSetChanged()
            }
        }

    }

    override fun onRecyclerClick(position: Int) {
        pos = position
        requestContactPermission()
    }

    private fun requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.CALL_PHONE
                    )
                ) {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setTitle("call  dial permission needed")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setMessage("Please enable call permission to dial.")
                    builder.setOnDismissListener(DialogInterface.OnDismissListener {
                        requestPermissions(
                            arrayOf(Manifest.permission.CALL_PHONE),
                            PERMISSIONS_REQUEST_PHONE_CALL
                        )
                    })
                    builder.show()
                } else {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.CALL_PHONE),
                        PERMISSIONS_REQUEST_PHONE_CALL
                    )
                }
            } else {
                dial()
            }
        } else {
            dial()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_PHONE_CALL -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dial()
                } else {
                    Toast.makeText(
                        this,
                        "You have disabled a call  dial permission",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun dial() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent1 = Intent(Intent.ACTION_CALL)
            intent1.data = Uri.parse("tel:" + list[pos].phoneNumber)
            startActivity(intent1)
        } else {
            requestContactPermission()
        }
    }

}