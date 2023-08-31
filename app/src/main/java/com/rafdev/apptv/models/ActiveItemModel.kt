package com.rafdev.apptv.models

data class ActiveItemModel(
    val is_ads: Boolean,
    val title: String,
    val description: String,
    val image: String,
    val time_playing: String
)