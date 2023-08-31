package com.rafdev.apptv.models

data class ResponseModel(
    val status: String,
    val code_error: Int,
    val msj: String,
    val t_found: Int,
    val t_data: Int,
    val data: List<VideoItemModel>
)

