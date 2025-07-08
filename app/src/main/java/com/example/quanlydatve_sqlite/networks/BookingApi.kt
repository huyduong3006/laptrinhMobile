package com.example.quanlydatve_sqlite.networks

import com.example.quanlydatve_sqlite.models.Booking
import com.example.quanlydatve_sqlite.models.BookingRequest
import com.example.quanlydatve_sqlite.models.PaymentInfoRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT


interface BookingApi {
    @POST("bookings")
    fun createBooking(@Body booking: BookingRequest): Call<Booking>

    @GET("bookings")
    fun getAllBookings(): Call<List<Booking>>

    @PUT("bookings/update-payment-info")
    fun updatePaymentInfo(@Body info: PaymentInfoRequest): Call<Void>
}
