package com.example.quanlydatve_sqlite.networks

import com.example.quanlydatve_sqlite.ChuyenBay
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query


interface ChuyenBayApi {
    @GET("chuyenbay")
    fun getAll(): Call<List<ChuyenBay>>

    @GET("chuyenbay/search")
    fun searchFlights(
        @Query("tu") tu: String,
        @Query("den") den: String,
        @Query("ngayDi") ngayDi: String,
        @Query("ngayVe") ngayVe: String
    ): Call<List<ChuyenBay>>

    @GET("chuyenbay/search-by-address")
    fun searchFlightsByAddress(
        @Query("tu") tu: String,
        @Query("den") den: String
    ): Call<List<ChuyenBay>>

    @GET("chuyenbay/featured")
    fun getFeatured(): Call<List<ChuyenBay>>

    @POST("chuyenbay")
    fun addFlight(@Body chuyenBay: ChuyenBay): Call<Void>

    @PUT("chuyenbay/{id}")
    fun updateFlight(@Path("id") id: Int, @Body chuyenBay: ChuyenBay): Call<Void>

    @DELETE("chuyenbay/{id}")
    fun deleteFlight(@Path("id") id: Int): Call<Void>
}