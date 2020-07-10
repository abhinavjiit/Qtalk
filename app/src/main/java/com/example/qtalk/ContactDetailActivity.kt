package com.example.qtalk

import android.Manifest
import android.content.ContentProviderOperation
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val PERMISSIONS_REQUEST_PHONE_CALL = 2

class ContactDetailActivity : AppCompatActivity(), View.OnClickListener {
    private var contactId: String? = null
    private var displayName: String? = null
    private var phonesList = ArrayList<String>()
    private var phoneNumberType = ArrayList<String>()
    private var emailList: ArrayList<String>? = null

    private lateinit var nameTextView: TextView
    private lateinit var userName: TextView
    private lateinit var phoneNumberTextView: TextView
    private lateinit var phoneNumberType1: TextView
    private lateinit var phoneNumber1: TextView
    private lateinit var phoneNumberType2: TextView
    private lateinit var phoneNumber2: TextView
    private lateinit var emailTextView: TextView
    private lateinit var email: TextView
    private lateinit var editNumber: EditText
    private lateinit var editName: EditText
    private lateinit var update: Button
    private lateinit var makeFav: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.contact_detail_activity)
        contactId = intent.getStringExtra("Contact_Id")
        displayName = intent.getStringExtra("DisPlayName")

        nameTextView = findViewById(R.id.nameTextView)
        userName = findViewById(R.id.userName)
        phoneNumberTextView = findViewById(R.id.PhoneNumberTextView)
        phoneNumberType1 = findViewById(R.id.PhoneNumberType1)
        phoneNumber1 = findViewById(R.id.PhoneNumber1)
        phoneNumberType2 = findViewById(R.id.PhoneNumberType2)
        phoneNumber2 = findViewById(R.id.PhoneNumber2)
        emailTextView = findViewById(R.id.emailTextView)
        email = findViewById(R.id.email)
        editNumber = findViewById(R.id.editNumber)
        editName = findViewById(R.id.editName)
        update = findViewById(R.id.update)
        makeFav = findViewById(R.id.makeFav)
        getDetailOfUser()
        phoneNumber1.setOnLongClickListener {
            requestContactPermission()
            true
        }
        update.setOnClickListener(this)
        makeFav.setOnClickListener(this)
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
                    builder.setOnDismissListener {
                        requestPermissions(
                            arrayOf(Manifest.permission.CALL_PHONE),
                            PERMISSIONS_REQUEST_PHONE_CALL
                        )
                    }
                    builder.show()
                } else {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(Manifest.permission.CALL_PHONE),
                        PERMISSIONS_REQUEST_PHONE_CALL
                    )
                }
            } else {
                dialNumber()
            }
        } else {
            dialNumber()
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
                    dialNumber()
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


    private fun dialNumber() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            val intent1 = Intent(Intent.ACTION_CALL)
            intent1.data = Uri.parse("tel:" + phoneNumber1.text.toString())
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
            phoneCursor?.let { phoneCursor1 ->


                while (phoneCursor1.moveToNext()) {
                    val phoneNumber: String =
                        phoneCursor1.getString(phoneCursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                    val type =
                        phoneCursor1.getString(phoneCursor1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE))
                    if (!type.isNullOrBlank()) {
                        val s = ContactsContract.CommonDataKinds.Phone.getTypeLabel(
                            resources,
                            Integer.parseInt(type),
                            ""
                        )
                        phoneNumberType.add(s.toString())
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
            phoneNumberType1.visibility = View.GONE
            phoneNumber2.visibility = View.GONE
            phoneNumber1.visibility = View.GONE
            phoneNumberType2.visibility = View.GONE
        } else {
            if (phonesList.size >= 1) {
                phoneNumber1.visibility = View.VISIBLE
                phoneNumberType1.visibility = View.VISIBLE
                phoneNumberType1.text = phoneNumberType[0]
                phoneNumber1.text = phonesList[0]
            }
            if (phonesList.size >= 2) {
                phoneNumber2.visibility = View.VISIBLE
                phoneNumberType2.visibility = View.VISIBLE
                phoneNumber2.text = phonesList[1]
                phoneNumberType2.text = phoneNumberType[1]
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

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.update -> {
                updateNumberAndName()
            }
            R.id.makeFav -> {
                val v = ContentValues()
                v.put(ContactsContract.Contacts.STARRED, 1)
                contentResolver.update(
                    ContactsContract.Contacts.CONTENT_URI,
                    v,
                    ContactsContract.Contacts._ID + "=?",
                    arrayOf(contactId)
                )
                Toast.makeText(this, "successfully added", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun updateNumberAndName() {
        val name = editName.text.toString().trim()
        val phoneNumber = editNumber.text.toString().trim()
        val ops = ArrayList<ContentProviderOperation>()
        val where = (ContactsContract.Data.CONTACT_ID + " = ? AND "
                + ContactsContract.Data.MIMETYPE + " = ?")
        val nameParams = arrayOf(
            contactId!!,
            ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE
        )
        val numberParams =
            arrayOf(
                contactId,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
            )
        if (name.isNotBlank()) {
            ops.add(
                ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, nameParams)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, name)
                    .build()
            )
            userName.text = name
        }
        if (phoneNumber.isNotBlank()) {
            ops.add(
                ContentProviderOperation.newUpdate(ContactsContract.Data.CONTENT_URI)
                    .withSelection(where, numberParams)
                    .withValue(ContactsContract.CommonDataKinds.Phone.DATA1, phoneNumber)
                    .build()
            )
            phoneNumber1.text = phoneNumber
        }
        contentResolver.applyBatch(ContactsContract.AUTHORITY, ops)
    }
    ///UPDATE %Contacts.CONTENT_URI% SET STARRED = 1 WHERE %Contacts.DISPLAY_NAME% = %strNamevalue%

}

