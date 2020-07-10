package com.example.qtalk

import android.Manifest
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout


const val PERMISSIONS_REQUEST_READ_CONTACTS = 1

class AllContactsListActivity : AppCompatActivity() {

    lateinit var viewPager: ViewPager
    lateinit var tabs: TabLayout
    lateinit var contactPagerAdapter: ContactPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.all_contacts_list_activity)
        viewPager = findViewById(R.id.viewPager)
        tabs = findViewById(R.id.tab)
        tabs.addTab(tabs.newTab().setText("All Contacts"))
        tabs.addTab(tabs.newTab().setText("Starred Contacts"))
        requestContactPermission()
    }


    private fun requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.READ_CONTACTS
                    ) || ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.WRITE_CONTACTS
                    )
                ) {
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setTitle("Read contacts access needed")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setMessage("Please enable access to contacts.")
                    builder.setOnDismissListener(DialogInterface.OnDismissListener {
                        requestPermissions(
                            arrayOf(
                                Manifest.permission.READ_CONTACTS,
                                Manifest.permission.WRITE_CONTACTS
                            ),
                            PERMISSIONS_REQUEST_READ_CONTACTS
                        )
                    })
                    builder.show()
                } else {
                    requestPermissions(
                        this,
                        arrayOf(
                            Manifest.permission.READ_CONTACTS,
                            Manifest.permission.WRITE_CONTACTS
                        ),
                        PERMISSIONS_REQUEST_READ_CONTACTS
                    )
                }
            } else {
                setAdapter()
            }
        } else {
            setAdapter()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PERMISSIONS_REQUEST_READ_CONTACTS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    setAdapter()
                } else {
                    Toast.makeText(
                        this,
                        "You have disabled a contacts permission",
                        Toast.LENGTH_LONG
                    ).show()
                    val builder: AlertDialog.Builder = AlertDialog.Builder(this)
                    builder.setTitle("Read contacts access needed")
                    builder.setPositiveButton(android.R.string.ok, null)
                    builder.setMessage("Please enable access to contacts.")
                    builder.setOnDismissListener(DialogInterface.OnDismissListener {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            requestPermissions(
                                arrayOf(
                                    Manifest.permission.READ_CONTACTS,
                                    Manifest.permission.WRITE_CONTACTS
                                ),
                                PERMISSIONS_REQUEST_READ_CONTACTS
                            )
                        }
                    })
                    builder.show()

                }
            }

        }
    }


    private fun setAdapter() {
        contactPagerAdapter = ContactPagerAdapter(supportFragmentManager)
        viewPager.adapter = contactPagerAdapter
        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab?.position!!

            }

            override fun onTabUnselected(p0: TabLayout.Tab?) {
            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem = tab?.position!!
            }

        }

        )
    }

}
