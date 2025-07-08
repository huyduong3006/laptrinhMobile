package com.example.quanlydatve_sqlite.activities

import NguoiDung
import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.networks.RetrofitClient
import com.example.quanlydatve_sqlite.networks.UserApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtHoTen: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtNgaySinh: EditText
    private lateinit var edtPhone: EditText
    private lateinit var btnDangKy: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dang_ky)

        // Ánh xạ view
        edtUsername = findViewById(R.id.edt_username)
        edtPassword = findViewById(R.id.edt_password)
        edtHoTen = findViewById(R.id.edtHoTen)
        edtEmail = findViewById(R.id.edt_email)
        edtNgaySinh = findViewById(R.id.edtNgaySinh)
        edtPhone = findViewById(R.id.edt_phone)
        btnDangKy = findViewById(R.id.btn_dang_ky)

        // Chọn ngày sinh bằng DatePicker
        edtNgaySinh.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                val ngay = if (d < 10) "0$d" else "$d"
                val thang = if (m + 1 < 10) "0${m + 1}" else "${m + 1}"
                edtNgaySinh.setText("$y-$thang-$ngay") // format yyyy-MM-dd
            }, year, month, day)

            datePicker.show()
        }

        // Xử lý đăng ký
        btnDangKy.setOnClickListener {
            val username = edtUsername.text.toString().trim()
            val password = edtPassword.text.toString().trim()
            val hoTen = edtHoTen.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val ngaySinh = edtNgaySinh.text.toString().trim()
            val phone = edtPhone.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên đăng nhập và mật khẩu", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val api = RetrofitClient.instance.create(UserApi::class.java)
            val nguoiDung = NguoiDung(
                id = 0,
                username = username,
                matKhau = password,
                hoTen = hoTen,
                ngaySinh = ngaySinh,
                email = email,
                phone = phone,
                role = "user"
            )

            api.register(nguoiDung).enqueue(object : Callback<NguoiDung> {
                override fun onResponse(call: Call<NguoiDung>, response: Response<NguoiDung>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@RegisterActivity, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                        finish() // Quay lại màn hình đăng nhập
                    } else {
                        Toast.makeText(this@RegisterActivity, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<NguoiDung>, t: Throwable) {
                    Toast.makeText(this@RegisterActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
