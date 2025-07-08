package com.example.quanlydatve_sqlite.activities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlydatve_sqlite.ChuyenBay
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.networks.ChuyenBayApi
import com.example.quanlydatve_sqlite.networks.RetrofitClient
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class TraCuuChuyenBayActivity : AppCompatActivity() {

    private lateinit var edtTu: MaterialAutoCompleteTextView
    private lateinit var edtDen: MaterialAutoCompleteTextView
    private lateinit var edtNgayDi: TextInputEditText
    private lateinit var edtNgayVe: TextInputEditText
    private lateinit var btnTimKiem: android.view.View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tra_cuu)

        edtTu = findViewById(R.id.edtTu)
        edtDen = findViewById(R.id.edtDen)
        edtNgayDi = findViewById(R.id.ediNgayDi)
        edtNgayVe = findViewById(R.id.ediNgayVe)
        btnTimKiem = findViewById(R.id.btnTimKiem)
        setupAutoComplete()

        edtNgayDi.setOnClickListener {
            showDatePicker { date ->
                edtNgayDi.setText(date)
            }
        }

        edtNgayVe.setOnClickListener {
            showDatePicker { date ->
                edtNgayVe.setText(date)
            }
        }

        btnTimKiem.setOnClickListener {
            val tu = edtTu.text.toString().trim()
            val den = edtDen.text.toString().trim()
            val ngayDi = edtNgayDi.text.toString().trim()
            val ngayVe = edtNgayVe.text.toString().trim()

            if (tu.isEmpty() || den.isEmpty() || ngayDi.isEmpty() || ngayVe.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            searchFlights(tu, den, ngayDi, ngayVe)
        }
    }
    private fun setupAutoComplete() {
        val cities = listOf("An Giang", "Bà Rịa - Vũng Tàu", "Bạc Liêu", "Bắc Giang", "Bắc Kạn", "Bắc Ninh",
            "Bến Tre", "Bình Dương", "Bình Định", "Bình Phước", "Bình Thuận", "Cà Mau",
            "Cao Bằng", "Cần Thơ", "Đà Nẵng", "Đắk Lắk", "Đắk Nông", "Điện Biên", "Đồng Nai",
            "Đồng Tháp", "Gia Lai", "Hà Giang", "Hà Nam", "Hà Nội", "Hà Tĩnh", "Hải Dương",
            "Hải Phòng", "Hậu Giang", "Hòa Bình", "Hưng Yên", "Khánh Hòa", "Kiên Giang",
            "Kon Tum", "Lai Châu", "Lạng Sơn", "Lào Cai", "Lâm Đồng", "Long An", "Nam Định",
            "Nghệ An", "Ninh Bình", "Ninh Thuận", "Phú Thọ", "Phú Yên", "Quảng Bình",
            "Quảng Nam", "Quảng Ngãi", "Quảng Ninh", "Quảng Trị", "Sóc Trăng", "Sơn La",
            "Tây Ninh", "Thái Bình", "Thái Nguyên", "Thanh Hóa", "Thừa Thiên Huế", "Tiền Giang",
            "TP. Hồ Chí Minh", "Trà Vinh", "Tuyên Quang", "Vĩnh Long", "Vĩnh Phúc", "Yên Bái", "Đà Lạt", "Phú Quốc"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, cities)
        edtTu.setAdapter(adapter)
        edtDen.setAdapter(adapter)
    }

    private fun showDatePicker(onDateSelected: (String) -> Unit) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val picker = DatePickerDialog(this, { _, y, m, d ->
            val dayStr = if (d < 10) "0$d" else "$d"
            val monthStr = if (m + 1 < 10) "0${m + 1}" else "${m + 1}"
            onDateSelected("$y-$monthStr-$dayStr")
        }, year, month, day)

        picker.show()
    }

    private fun searchFlights(tu: String, den: String, ngayDi: String, ngayVe: String) {
        val api = RetrofitClient.instance.create(ChuyenBayApi::class.java)
        api.searchFlights(tu, den, ngayDi, ngayVe).enqueue(object : Callback<List<ChuyenBay>> {
            override fun onResponse(call: Call<List<ChuyenBay>>, response: Response<List<ChuyenBay>>) {
                if (response.isSuccessful) {
                    val results = response.body()
                    if (!results.isNullOrEmpty()) {
                        val intent = Intent(this@TraCuuChuyenBayActivity, DanhSachChuyenBayDieuKienActivity::class.java)
                        intent.putExtra("dsChuyenBay", ArrayList(results))
                        startActivity(intent)
                    } else {
                        Toast.makeText(this@TraCuuChuyenBayActivity, "Không tìm thấy chuyến bay phù hợp", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("TraCuuChuyenBay", "Lỗi server code: ${response.code()}, body: $errorBody")

                    val message = when (response.code()) {
                        400 -> "Yêu cầu không hợp lệ (400)"
                        404 -> "Không tìm thấy API (404)"
                        500 -> "Lỗi máy chủ (500)"
                        else -> "Lỗi server: ${response.code()}"
                    }

                    Toast.makeText(this@TraCuuChuyenBayActivity, "$message\nChi tiết: $errorBody", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<List<ChuyenBay>>, t: Throwable) {
                Log.e("TraCuuChuyenBay", "Lỗi kết nối", t)
                Toast.makeText(this@TraCuuChuyenBayActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}
