package com.example.quanlydatve_sqlite.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlydatve_sqlite.ChuyenBay
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.adapters.AdminChuyenBayAdapter
import com.example.quanlydatve_sqlite.networks.ChuyenBayApi
import com.example.quanlydatve_sqlite.networks.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.awaitResponse

class AdminQuanLyChuyenBay : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AdminChuyenBayAdapter
    private val danhSachChuyenBay = mutableListOf<ChuyenBay>()
    private lateinit var chuyenbayApi: ChuyenBayApi
    private lateinit var addFlightLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_qlchuyenbay)

        recyclerView = findViewById(R.id.recyclerViewChuyenBayAdmin)
        recyclerView.layoutManager = LinearLayoutManager(this)

        findViewById<ImageView>(R.id.icBack).setOnClickListener { finish() }

        // ✅ Khởi tạo launcher
        addFlightLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK) {
                loadChuyenBay()
            }
        }

        // ✅ Dùng launcher để start AddChuyenBayActivity
        findViewById<View>(R.id.btnAddChuyenBay).setOnClickListener {
            val intent = Intent(this, AddChuyenBayActivity::class.java)
            addFlightLauncher.launch(intent)
        }

        chuyenbayApi = RetrofitClient.instance.create(ChuyenBayApi::class.java)

        adapter = AdminChuyenBayAdapter(danhSachChuyenBay) {
            loadChuyenBay()
        }
        recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        loadChuyenBay()
    }

    private fun loadChuyenBay() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    chuyenbayApi.getAll().awaitResponse()
                }
                if (response.isSuccessful) {
                    val list = response.body() ?: emptyList()
                    danhSachChuyenBay.clear()
                    danhSachChuyenBay.addAll(list)
                    adapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        this@AdminQuanLyChuyenBay,
                        "Lỗi: ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@AdminQuanLyChuyenBay, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
