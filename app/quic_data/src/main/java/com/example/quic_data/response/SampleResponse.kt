package com.example.quic_data.response

import com.google.gson.annotations.SerializedName

data class SampleResponse(
    @SerializedName("per_page")
    val perPage: Long,
    val total: Long,
    @SerializedName("total_pages")
    val totalPages: Long,
    val data: List<Datum>
) {
    data class Datum(
        val id: Long,
        val email: String,
        @SerializedName("first_name")
        val firstName: String,
        @SerializedName("last_name")
        val lastName: String,
        val avatar: String
    )

}