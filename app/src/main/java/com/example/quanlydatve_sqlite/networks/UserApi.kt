package com.example.quanlydatve_sqlite.networks

import NguoiDung
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

data class LoginRequest(
    val tenDangNhap: String,
    val matKhau: String
)

data class UpdateUserRequest(
    val hoTen: String,
    val ngaySinh: String,
    val email: String,
    val phone: String,
    val matKhau: String
)

@JvmSuppressWildcards
interface UserApi {
    @POST("auth/login")   // tương đương /api/login
    fun login(@Body request: LoginRequest): Call<NguoiDung>

    @POST("auth/register")  // tương đương /api/register
    fun register(@Body nguoiDung: NguoiDung): Call<NguoiDung>

    @POST("auth/logout")  // tương đương /api/logout
    fun logout(): Call<Void>

    @GET("user/{username}")  // tương đương /api/user/{username}
    fun getUserByUsername(@Path("username") username: String): Call<NguoiDung>

    @PUT("user/{username}")
    fun updateUser(
        @Path("username") username: String,
        @Body userUpdate: UpdateUserRequest
    ): Call<Void>

}
