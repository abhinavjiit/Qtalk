package com.example.qtalk.model

import android.os.Parcel
import android.os.Parcelable

data class ContactsInfo(
    var contactId: String? = null,
    var displayName: String? = null,
    var phoneNumber: String? = null,
    var email: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(contactId)
        parcel.writeString(displayName)
        parcel.writeString(phoneNumber)
        parcel.writeString(email)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ContactsInfo> {
        override fun createFromParcel(parcel: Parcel): ContactsInfo {
            return ContactsInfo(parcel)
        }

        override fun newArray(size: Int): Array<ContactsInfo?> {
            return arrayOfNulls(size)
        }
    }
}
