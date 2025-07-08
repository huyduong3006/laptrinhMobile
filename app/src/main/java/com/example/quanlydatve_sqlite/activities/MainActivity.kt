package com.example.quanlydatve_sqlite.activities

import NguoiDung
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quanlydatve_sqlite.ChuyenBay
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.adapters.ChuyenBayAdapter
import com.example.quanlydatve_sqlite.networks.ChuyenBayApi
import com.example.quanlydatve_sqlite.networks.RetrofitClient
import com.example.quanlydatve_sqlite.networks.UserApi
import com.example.quanlydatve_sqlite.utils.SharedPrefHelper
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewChuyenBay: RecyclerView
    private lateinit var chuyenBayAdapter: ChuyenBayAdapter
    private val chuyenBayList = mutableListOf<ChuyenBay>()

    private lateinit var btnFlightSearch: AppCompatButton
    private lateinit var btnProfile: Button

    private lateinit var txtTenAdmin: TextView
    private lateinit var txtNgaySinh: TextView
    private lateinit var imgHinhAdmin: ImageView

    companion object {
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerViewChuyenBay = findViewById(R.id.recyclerViewChuyenBay)
        btnFlightSearch = findViewById(R.id.btn_flight_search)
        btnProfile = findViewById(R.id.btnProfile)

        txtTenAdmin = findViewById(R.id.tenAdmin)
        txtNgaySinh = findViewById(R.id.ngaySinh)
        imgHinhAdmin = findViewById(R.id.hinhAdmin)

        recyclerViewChuyenBay.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        chuyenBayAdapter = ChuyenBayAdapter(chuyenBayList) { chuyenBay ->
            val intent = Intent(this, DanhSachChuyenBayDieuKienActivity::class.java)
            intent.putExtra("tu", chuyenBay.tu)
            intent.putExtra("den", chuyenBay.den)
            startActivity(intent)
        }

        recyclerViewChuyenBay.adapter = chuyenBayAdapter

        val username = SharedPrefHelper.getUsername(this)
        Log.d(TAG, "Username lấy từ SharedPreferences: $username")

        if (username.isNullOrBlank()) {
            Log.w(TAG, "Username rỗng hoặc null, chưa đăng nhập")
            txtTenAdmin.text = "Chưa đăng nhập"
            txtNgaySinh.text = "Không rõ"
            Glide.with(this).load(R.drawable.user_avatar).into(imgHinhAdmin)
        } else {
            val api = RetrofitClient.instance.create(UserApi::class.java)
            api.getUserByUsername(username).enqueue(object : Callback<NguoiDung> {
                override fun onResponse(call: Call<NguoiDung>, response: Response<NguoiDung>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        txtTenAdmin.text = user?.hoTen ?: "Tên nhân viên"
                        txtNgaySinh.text = formatNgaySinh(user?.ngaySinh)
                        user?.let { SharedPrefHelper.saveUser(this@MainActivity, it) }
                        Glide.with(this@MainActivity).load(R.drawable.user_avatar).into(imgHinhAdmin)
                    } else {
                        Toast.makeText(this@MainActivity, "Không lấy được thông tin người dùng", Toast.LENGTH_SHORT).show()
                        txtTenAdmin.text = "Tên nhân viên"
                        txtNgaySinh.text = "Không rõ"
                        Glide.with(this@MainActivity).load(R.drawable.user_avatar).into(imgHinhAdmin)
                    }
                }

                override fun onFailure(call: Call<NguoiDung>, t: Throwable) {
                    Log.e(TAG, "Lỗi kết nối API", t)
                    Toast.makeText(this@MainActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
                    txtTenAdmin.text = "Tên nhân viên"
                    txtNgaySinh.text = "Không rõ"
                    Glide.with(this@MainActivity).load(R.drawable.user_avatar).into(imgHinhAdmin)
                }
            })
        }

        fetchFeaturedFlights()

        btnFlightSearch.setOnClickListener {
            startActivity(Intent(this, TraCuuChuyenBayActivity::class.java))
        }

        btnProfile.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> true

                R.id.nav_search -> {
                    startActivity(Intent(this, TraCuuChuyenBayActivity::class.java))
                    true
                }

                R.id.nav_ticket -> {
                    val role = SharedPrefHelper.getRole(this)
                    if (role != "user") {
                        Toast.makeText(this, "Bạn không có chức năng này", Toast.LENGTH_SHORT).show()
                        return@setOnItemSelectedListener true
                    }
                    startActivity(Intent(this, LichSuDatVeActivity::class.java))
                    true
                }

                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }

                else -> false
            }
        }
        bottomNav.selectedItemId = R.id.nav_home

    }

    private fun fetchFeaturedFlights() {
        val api = RetrofitClient.instance.create(ChuyenBayApi::class.java)
        api.getFeatured().enqueue(object : Callback<List<ChuyenBay>> {
            override fun onResponse(call: Call<List<ChuyenBay>>, response: Response<List<ChuyenBay>>) {
                if (response.isSuccessful) {
                    response.body()?.let { flights ->
                        chuyenBayList.clear()
                        chuyenBayList.addAll(flights)
                        chuyenBayAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e(TAG, "Lấy chuyến bay thất bại: code=${response.code()}")
                    Toast.makeText(this@MainActivity, "Lấy chuyến bay thất bại", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ChuyenBay>>, t: Throwable) {
                Log.e(TAG, "Lỗi kết nối chuyến bay", t)
                Toast.makeText(this@MainActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun formatNgaySinh(ngaySinh: String?): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            inputFormat.timeZone = TimeZone.getTimeZone("UTC")

            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = inputFormat.parse(ngaySinh ?: return "Không rõ")
            outputFormat.format(date!!)
        } catch (e: Exception) {
            Log.e(TAG, "Lỗi format ngày sinh", e)
            "Không rõ"
        }
    }

}
