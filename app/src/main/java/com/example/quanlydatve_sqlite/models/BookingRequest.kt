package com.example.quanlydatve_sqlite.models

data class BookingRequest(
    val userId: Int,
    val tu: String,
    val den: String,
    val ngayDi: String,
    val ngayVe: String,
    val quantity: Int,
    val ticketType: String,
    val price: Double,
    val tax: Double,
    val totalPrice: Double,
    val paymentMethod: String
)
