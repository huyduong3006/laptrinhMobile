package com.example.quanlydatve_sqlite.models

data class PaymentInfoRequest(
    val bookingId: Int,
    val tenNganHang: String,
    val soTaiKhoan: String,
    val noiDung: String
)
