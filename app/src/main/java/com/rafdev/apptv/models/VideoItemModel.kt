package com.rafdev.apptv.models

data class VideoItemModel(
 val key: String,
 val key_live: String,
 val name_live: String,
 val background_image: String,
 val logo: String,
 val color: String,
 val order: Int,
 val is_playlist: Boolean,
 val title: String,
 val timezone: String,
 val active: Boolean,
 val live: String,
 val is_schedule: Boolean,
 val active_item_data: ActiveItemModel,
 val folder: String,
 val vast: String,
 val ads: String,
 val m3u8: String
)