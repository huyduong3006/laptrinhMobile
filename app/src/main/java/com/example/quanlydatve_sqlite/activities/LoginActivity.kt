package com.example.quanlydatve_sqlite.activities

import NguoiDung
import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.networks.LoginRequest
import com.example.quanlydatve_sqlite.networks.RetrofitClient
import com.example.quanlydatve_sqlite.networks.UserApi
import com.example.quanlydatve_sqlite.utils.SharedPrefHelper
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var edtUsername: TextInputEditText
    private lateinit var edtPassword: TextInputEditText
    private lateinit var btnDangNhap: Button
    private lateinit var btnDangKy: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        SharedPrefHelper.clearUser(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dang_nhap)

        edtUsername = findViewById(R.id.edt_username)
        edtPassword = findViewById(R.id.edt_password)
        btnDangNhap = findViewById(R.id.btn_dang_nhap)
        val btnDangKy = findViewById<TextView>(R.id.btn_dang_ky)
        val spannable = Html.fromHtml("Chưa có tài khoản? <u><font color='#2196F3'>Đăng ký</font></u>")
        btnDangKy.text = spannable
        progressBar = findViewById(R.id.progressBar)

        if (SharedPrefHelper.isLoggedIn(this)) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        btnDangKy.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        btnDangNhap.setOnClickListener {
            val username = edtUsername.text.toString().trim()
            val password = edtPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            progressBar.visibility = View.VISIBLE
            login(username, password)
        }
    }

    private fun login(username: String, password: String) {
        val userApi = RetrofitClient.instance.create(UserApi::class.java)
        val request = LoginRequest(tenDangNhap = username, matKhau = password) // Đặt đúng key khớp backend

        userApi.login(request).enqueue(object : Callback<NguoiDung> {
            override fun onResponse(call: Call<NguoiDung>, response: Response<NguoiDung>) {
                progressBar.visibility = View.GONE

                val code = response.code()
                val errorBody = response.errorBody()?.string()
                val body = response.body()
                println("DEBUG: HTTP $code")
                println("DEBUG: Response body = $body")
                println("DEBUG: Error body = $errorBody")

                if (response.isSuccessful) {
                    val nguoiDung = response.body()
                    if (nguoiDung != null) {
                        Toast.makeText(this@LoginActivity, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                        SharedPrefHelper.saveUser(this@LoginActivity, nguoiDung)

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Dữ liệu người dùng trống", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Sai tài khoản hoặc mật khẩu", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NguoiDung>, t: Throwable) {
                progressBar.visibility = View.GONE
                t.printStackTrace()
                Toast.makeText(this@LoginActivity, "Lỗi kết nối: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

}
