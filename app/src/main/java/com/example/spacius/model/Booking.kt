package com.example.spacius.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Booking(
    val bookingId: String = "",
    val spaceName: String = "",
    val date: String = "",
    val time: String = ""
) : Parcelable