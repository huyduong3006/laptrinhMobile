package com.example.quanlydatve_sqlite.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.adapters.BookingAdapter
import com.example.quanlydatve_sqlite.models.Booking
import com.example.quanlydatve_sqlite.networks.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LichSuDatVeActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BookingAdapter
    private val bookingList = mutableListOf<Booking>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_danh_sach_ve)

        recyclerView = findViewById(R.id.recyclerViewLichSu)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = BookingAdapter(bookingList)
        recyclerView.adapter = adapter

        fetchBookingFromApi()

        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btnTrangChu).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        findViewById<Button>(R.id.btnGanDay).setOnClickListener {
            Toast.makeText(this, "Chức năng đang phát triển", Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchBookingFromApi() {
        RetrofitClient.bookingApi.getAllBookings().enqueue(object : Callback<List<Booking>> {
            override fun onResponse(call: Call<List<Booking>>, response: Response<List<Booking>>) {
                if (response.isSuccessful) {
                    bookingList.clear()
                    response.body()?.let { bookingList.addAll(it) }
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@LichSuDatVeActivity, "Không thể tải dữ liệu từ server", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Booking>>, t: Throwable) {
                Toast.makeText(this@LichSuDatVeActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }
}
