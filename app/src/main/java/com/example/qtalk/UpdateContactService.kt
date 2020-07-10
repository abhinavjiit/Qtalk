package com.example.qtalk

import android.app.Service
import android.content.Intent
import android.database.ContentObserver
import android.database.Cursor
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.provider.ContactsContract
import android.util.Log
import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class UpdateContactService : Service() {

    var mContact: Int = 0
    var cursor: Cursor? = null

    override fun onBind(p0: Intent?): IBinder? {
        return null;
    }

    override fun onCreate() {
        super.onCreate()
        mContact = getContactCount()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        contentResolver.registerContentObserver(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            true,
            mObserver
        )
        return START_STICKY
    }


    private fun getContactCount(): Int {
        try {
            cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                null,
                null,
                null
            )
            when (cursor) {
                null -> {
                    return cursor?.count!!
                }
                else -> {
                    cursor!!.close()
                    return 0
                }
            }
        } catch (e: Exception) {
        } finally {
            cursor?.close()
        }
        return 0
    }

    private val mObserver: ContentObserver = object : ContentObserver(Handler()) {
        override fun onChange(selfChange: Boolean) {
            this.onChange(selfChange, null)
        }

        override fun onChange(selfChange: Boolean, uri: Uri?) {
            Observable.fromCallable { updateDeleteContact() }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<String> {
                    override fun onComplete() {
                        stopSelf()

                    }

                    override fun onSubscribe(d: Disposable) {
                    }

                    override fun onNext(t: String) {
                        Log.d("UpdateTag", t)
                    }

                    override fun onError(e: Throwable) {
                    }

                })

        }
    }


    private fun updateDeleteContact(): String {
        val updatedContact = getContactCount()
        return when {
            updatedContact > mContact -> {
                "Add"
            }
            updatedContact < mContact -> {

                "Delete"
            }
            else -> {
                "update"
            }
        }

    }


}