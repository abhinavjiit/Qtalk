package com.example.qtalk

import android.Manifest
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val PERMISSIONS_REQUEST_PHONE_CALL = 2

class ContactDetailActivity : AppCompatActivity() {
    private var contactId: String? = null
    private var displayName: String? = null
    private var phonesList = ArrayList<String>()
    private var PhoneNumberType = ArrayList<String>()
    private var emailList: ArrayList<String>? = null

    private lateinit var nameTextView: TextView
    private lateinit var userName: TextView
    private lateinit var PhoneNumberTextView: TextView
    private lateinit var PhoneNumberType1: TextView
    private lateinit var PhoneNumber1: TextView
    private lateinit var PhoneNumberType2: TextView
    private lateinit var PhoneNumber2: TextView
    private lateinit var emailTextView: TextView
    private lateinit var email: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contact_detail_activity)
        contactId = intent.getStringExtra("Contact_Id")
        displayName = intent.getStringExtra("DisPlayName")

        nameTextView = findViewById(R.id.nameTextView)
        userName = findViewById(R.id.userName)
        PhoneNumberTextView = findViewById(R.id.PhoneNumberTextView)
        PhoneNumberType1 = findViewById(R.id.PhoneNumberType1)
        PhoneNumber1 = findViewById(R.id.PhoneNumber1)
        PhoneNumberType2 = findViewById(R.id.PhoneNumberType2)
        PhoneNumber2 = findViewById(R.id.PhoneNumber2)
        emailTextView = findViewById(R.id.emailTextView)
        email = findViewById(R.id.email)
        getDetailOfUser()
        PhoneNumber1.setOnLongClickListener {
            requestContactPermission()
            true
        }

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
                dail()
            }
        } else {
            dail()
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
                    dail()
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


    private fun dail() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent1 = Intent(Intent.ACTION_CALL)
            intent1.data = Uri.parse("tel:"+PhoneNumber1.text.toString())
            startActivity(intent1)
        } else {
            requestContactPermission()
        }

    }


    private fun getDetailOfUser() {
        contactId?.let {
            val phoneCursor: Cursor? = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                arrayOf<String?>(it),
                null
            )
            phoneCursor?.let { phoneCursor ->


                while (phoneCursor.moveToNext()) {
                    val phoneNumber: String =
                        phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val type =
                        phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))
                    if (!type.isNullOrBlank()) {
                        val s = ContactsContract.CommonDataKinds.Phone.getTypeLabel(
                            resources,
                            Integer.parseInt(type),
                            ""
                        )
                        PhoneNumberType.add(s.toString())
                    }
                    if (phoneNumber.contains("+91")) {
                        val dialerPhoneNumber = phoneNumber.removePrefix("+91").trim()

                        phonesList.add(dialerPhoneNumber)
                    } else {
                        phonesList.add(phoneNumber)
                    }

                }


            }



            Log.d("number", phonesList.toString())
            phoneCursor?.close()

            val emailCursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                arrayOf<String?>(it),
                null
            )
            emailList = ArrayList()
            while (emailCursor?.moveToNext()!!) {

                val email =
                    emailCursor.getString(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA))
                val type =
                    emailCursor.getInt(emailCursor.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
                val s =
                    ContactsContract.CommonDataKinds.Email.getTypeLabel(resources, type, "")
                emailList?.add(email)
            }
            emailCursor.close()
        }
        setValuesToUi()
    }


    private fun setValuesToUi() {
        userName.text = displayName

        if (phonesList.isNullOrEmpty()) {
            PhoneNumberType1.visibility = View.GONE
            PhoneNumber2.visibility = View.GONE
            PhoneNumber1.visibility = View.GONE
            PhoneNumberType2.visibility = View.GONE
        } else {
            if (phonesList.size >= 1) {
                PhoneNumber1.visibility = View.VISIBLE
                PhoneNumberType1.visibility = View.VISIBLE
                PhoneNumberType1.text = PhoneNumberType[0]
                PhoneNumber1.text = phonesList[0]
            }
            if (phonesList.size >= 2) {
                PhoneNumber2.visibility = View.VISIBLE
                PhoneNumberType2.visibility = View.VISIBLE
                PhoneNumber2.text = phonesList[1]
                PhoneNumberType2.text = PhoneNumberType[1]
            }
        }
        if (emailList.isNullOrEmpty()) {
            email.visibility = View.GONE
            emailTextView.visibility = View.GONE
        } else {
            email.visibility = View.VISIBLE
            emailTextView.visibility = View.VISIBLE
            email.text = emailList?.get(0)
        }

    }

}

