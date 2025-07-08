package com.example.quanlydatve_sqlite.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlydatve_sqlite.ChuyenBay
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.adapters.ChuyenBayAdapterDieuKien
import com.example.quanlydatve_sqlite.networks.ChuyenBayApi
import com.example.quanlydatve_sqlite.networks.RetrofitClient
import com.example.quanlydatve_sqlite.utils.SharedPrefHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DanhSachChuyenBayDieuKienActivity : AppCompatActivity() {
    private lateinit var recyclerFlights: RecyclerView
    private lateinit var adapter: ChuyenBayAdapterDieuKien
    private var chuyenBayList = ArrayList<ChuyenBay>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dscb_phu_hop)

        recyclerFlights = findViewById(R.id.recyclerViewChuyenBayPhuHop)
        adapter = ChuyenBayAdapterDieuKien(chuyenBayList) { flight ->
            val role = SharedPrefHelper.getRole(this)
            if (role != "user") {
                Toast.makeText(this, "Bạn không có chức năng này", Toast.LENGTH_SHORT).show()
                return@ChuyenBayAdapterDieuKien
            }

            val intent = Intent(this, DatVeActivity::class.java).apply {
                putExtra("EXTRA_TU", flight.tu)
                putExtra("EXTRA_DEN", flight.den)
                putExtra("EXTRA_NGAY_DI", flight.ngayDi)
                putExtra("EXTRA_NGAY_VE", flight.ngayVe)
                putExtra("EXTRA_GIA", flight.giaVe)
            }
            startActivity(intent)
        }


        recyclerFlights.layoutManager = GridLayoutManager(this, 2)
        recyclerFlights.setHasFixedSize(true)
        recyclerFlights.adapter = adapter

        val data = intent.getSerializableExtra("dsChuyenBay") as? ArrayList<ChuyenBay>
        if (data != null) {
            chuyenBayList.clear()
            chuyenBayList.addAll(data)
            adapter.notifyDataSetChanged()
            return
        }

        val tu = intent.getStringExtra("tu")
        val den = intent.getStringExtra("den")
        if (!tu.isNullOrEmpty() && !den.isNullOrEmpty()) {
            fetchChuyenBayTuAPI(tu, den)
        }

    }

    private fun fetchChuyenBayTuAPI(tu: String, den: String) {
        val apiService = RetrofitClient.instance.create(ChuyenBayApi::class.java)
        val call = apiService.searchFlightsByAddress(tu, den)

        call.enqueue(object : Callback<List<ChuyenBay>> {
            override fun onResponse(call: Call<List<ChuyenBay>>, response: Response<List<ChuyenBay>>) {
                if (response.isSuccessful && response.body() != null) {
                    chuyenBayList.clear()
                    chuyenBayList.addAll(response.body()!!)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(this@DanhSachChuyenBayDieuKienActivity, "Không tìm thấy chuyến bay", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<ChuyenBay>>, t: Throwable) {
                Toast.makeText(this@DanhSachChuyenBayDieuKienActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
                Log.e("API_ERROR", t.toString())
            }
        })
    }

}
