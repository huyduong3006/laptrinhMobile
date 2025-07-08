package com.example.quanlydatve_sqlite.activities

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.MotionEvent
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.quanlydatve_sqlite.ChuyenBay
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.networks.ChuyenBayApi
import com.example.quanlydatve_sqlite.networks.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditChuyenBayActivity : AppCompatActivity() {

    private lateinit var edtTu: EditText
    private lateinit var edtDen: EditText
    private lateinit var edtNgayDi: EditText
    private lateinit var edtNgayVe: EditText
    private lateinit var edtGiaVe: EditText
    private lateinit var imgHinh: ImageView
    private lateinit var btnSave: Button

    private var idChuyenBay: Int = -1
    private var hinhAnh: String = ""
    private lateinit var chuyenBayApi: ChuyenBayApi

    private val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.edit_chuyen_bay)

        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        chuyenBayApi = RetrofitClient.instance.create(ChuyenBayApi::class.java)

        edtTu = findViewById(R.id.diemDi)
        edtDen = findViewById(R.id.diemDen)
        edtNgayDi = findViewById(R.id.ngayDi)
        edtNgayVe = findViewById(R.id.ngayVe)
        edtGiaVe = findViewById(R.id.giaVe)
        imgHinh = findViewById(R.id.hinhChuyenBay)
        btnSave = findViewById(R.id.btnSave)

        // Disable editing for Điểm đi / Điểm đến
        edtTu.isEnabled = false
        edtTu.isFocusable = false
        edtTu.isClickable = false

        edtDen.isEnabled = false
        edtDen.isFocusable = false
        edtDen.isClickable = false

        intent?.let {
            idChuyenBay = it.getIntExtra("id", -1)
            edtTu.setText(it.getStringExtra("tu"))
            edtDen.setText(it.getStringExtra("den"))

            // Convert yyyy-MM-dd -> dd/MM/yyyy nếu cần
            val ngayDi = formatDate(it.getStringExtra("ngayDi") ?: "")
            val ngayVe = formatDate(it.getStringExtra("ngayVe") ?: "")
            edtNgayDi.setText(ngayDi)
            edtNgayVe.setText(ngayVe)

            edtGiaVe.setText(it.getDoubleExtra("giaVe", 0.0).toString())
            hinhAnh = it.getStringExtra("hinhAnh") ?: ""
            loadImage(hinhAnh)
        }

        edtNgayDi.setOnClickListener { showDatePicker(edtNgayDi) }
        edtNgayVe.setOnClickListener { showDatePicker(edtNgayVe) }

        btnSave.setOnClickListener {
            val ngayDiStr = edtNgayDi.text.toString().trim()
            val ngayVeStr = edtNgayVe.text.toString().trim()
            val giaVe = edtGiaVe.text.toString().trim().toDoubleOrNull()

            if (ngayDiStr.isEmpty() || ngayVeStr.isEmpty() || giaVe == null) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin hợp lệ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val ngayDi = revertDate(ngayDiStr)
            val ngayVe = revertDate(ngayVeStr)

            val updated = ChuyenBay(
                id = idChuyenBay,
                tu = edtTu.text.toString(),
                den = edtDen.text.toString(),
                ngayDi = ngayDi,
                ngayVe = ngayVe,
                giaVe = giaVe,
                hinhAnh = hinhAnh
            )

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = chuyenBayApi.updateFlight(idChuyenBay, updated).awaitResponse()
                    if (response.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(this@EditChuyenBayActivity, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(this@EditChuyenBayActivity, "Lỗi cập nhật: ${response.code()}", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    runOnUiThread {
                        Toast.makeText(this@EditChuyenBayActivity, "Lỗi: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun showDatePicker(targetEditText: EditText) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format("%02d/%02d/%04d", dayOfMonth, month + 1, year)
                targetEditText.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    private fun loadImage(hinh: String) {
        if (hinh.startsWith("http")) {
            Glide.with(this).load(hinh).into(imgHinh)
        } else if (File(hinh).exists()) {
            Glide.with(this).load(File(hinh)).into(imgHinh)
        } else {
            val resId = resources.getIdentifier(hinh, "drawable", packageName)
            Glide.with(this).load(if (resId != 0) resId else R.drawable.user_avatar).into(imgHinh)
        }
    }

    private fun formatDate(dateStr: String): String {
        return try {
            val date = inputFormat.parse(dateStr)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            dateStr
        }
    }

    private fun revertDate(dateStr: String): String {
        return try {
            val date = outputFormat.parse(dateStr)
            inputFormat.format(date!!)
        } catch (e: Exception) {
            dateStr
        }
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            currentFocus!!.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }
}
