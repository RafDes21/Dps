package models

data class DataModel(
    val status: String,
    val code_error: Int,
    val msj: String,
    val t_found: Int,
    val t_data: Int,
    val data: List<DataItem>
)

data class DataItem(
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
    val active_item_data: ActiveItemData,
    val folder: String,
    val vast: String,
    val ads: String,
    val m3u8: String
)

data class ActiveItemData(
    val is_ads: Boolean,
    val title: String,
    val description: String,
    val image: String,
    val time_playing: String
)