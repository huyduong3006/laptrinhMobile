package com.example.quanlydatve_sqlite

import com.google.gson.annotations.SerializedName

data class ChuyenBay(
    val id: Int = 0,
    val ngayDi: String,
    val ngayVe: String,
    val tu: String,
    val den: String,
    val hinhAnh: String,
    @SerializedName("gia") val giaVe: Double
) : java.io.Serializable

{
    fun getHinhAnhResId(): Int {
        return when (hinhAnh.lowercase()) {
            "phuquoc" -> R.drawable.phuquoc
            "dalat" -> R.drawable.dalat
            "nhatrang" -> R.drawable.nhatrang
            "danang" -> R.drawable.danang
            "hanoi" -> R.drawable.hanoi
            "vungtau" -> R.drawable.vungtau
            "quangngai" -> R.drawable.quangngai
            else -> R.drawable.hanoi
        }
    }
}
