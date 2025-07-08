package com.example.quanlydatve_sqlite.activities

import NguoiDung
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.networks.RetrofitClient
import com.example.quanlydatve_sqlite.networks.UserApi
import com.example.quanlydatve_sqlite.utils.SharedPrefHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var tvName: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvPhone: TextView
    private lateinit var tvNgaySinh: TextView
    private lateinit var tvMatKhau: TextView
    private lateinit var btnEdit: Button
    private lateinit var btnQuanLyChuyenBay: Button
    private lateinit var btnLogout: Button

    private lateinit var userApi: UserApi
    private var currentUser: NguoiDung? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        tvName = findViewById(R.id.tv_name)
        tvEmail = findViewById(R.id.tv_email)
        tvPhone = findViewById(R.id.tv_sodienthoai)
        tvNgaySinh = findViewById(R.id.tv_ngaysinh)
        tvMatKhau = findViewById(R.id.tv_matkhau)
        btnEdit = findViewById(R.id.btnEdit)
        btnQuanLyChuyenBay = findViewById(R.id.btnQuanLyChuyenBay)
        btnLogout = findViewById(R.id.btnLogout)

        userApi = RetrofitClient.instance.create(UserApi::class.java)

        val username = SharedPrefHelper.getUsername(this)
        if (username == null) {
            Toast.makeText(this, "Chưa đăng nhập", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadUserInfo(username)

        btnEdit.setOnClickListener {
            currentUser?.username?.let { tenDangNhap ->
                val intent = Intent(this, EditUserActivity::class.java)
                intent.putExtra("username", tenDangNhap)
                startActivity(intent)
            }
        }

        btnQuanLyChuyenBay.setOnClickListener {
            val role = SharedPrefHelper.getRole(this)
            if (role == "user") {
                Toast.makeText(this, "Bạn không có đủ thẩm quyền", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val username = SharedPrefHelper.getUsername(this)
            if (username == null) {
                Toast.makeText(this, "Chưa đăng nhập!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            userApi.getUserByUsername(username).enqueue(object : Callback<NguoiDung> {
                override fun onResponse(call: Call<NguoiDung>, response: Response<NguoiDung>) {
                    if (response.isSuccessful) {
                        val user = response.body()
                        if (user?.role == "admin") {
                            startActivity(Intent(this@ProfileActivity, AdminQuanLyChuyenBay::class.java))
                        } else {
                            Toast.makeText(this@ProfileActivity, "Bạn không có quyền truy cập!", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@ProfileActivity, "Lỗi khi lấy thông tin người dùng!", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<NguoiDung>, t: Throwable) {
                    Toast.makeText(this@ProfileActivity, "Lỗi mạng: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
        btnLogout.setOnClickListener {
            userApi.logout().enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        SharedPrefHelper.clear(this@ProfileActivity)

                        val intent = Intent(this@ProfileActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@ProfileActivity, "Lỗi khi đăng xuất", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@ProfileActivity, "Lỗi mạng: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

    }

    private fun loadUserInfo(username: String) {
        userApi.getUserByUsername(username).enqueue(object : Callback<NguoiDung> {
            override fun onResponse(call: Call<NguoiDung>, response: Response<NguoiDung>) {
                if (response.isSuccessful) {
                    currentUser = response.body()
                    currentUser?.let { user ->
                        tvName.text = user.hoTen ?: user.username
                        tvEmail.text = user.email ?: ""
                        tvPhone.text = user.phone ?: ""
                        tvNgaySinh.text = formatNgaySinh(user.ngaySinh)
                        tvMatKhau.text = "**********"
                    }
                } else {
                    Toast.makeText(this@ProfileActivity, "Không tải được thông tin người dùng", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NguoiDung>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "Lỗi mạng: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        val username = SharedPrefHelper.getUsername(this)
        username?.let { loadUserInfo(it) }
    }

    private fun formatNgaySinh(ngaySinhRaw: String?): String {
        return try {
            val isoFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            isoFormat.timeZone = TimeZone.getTimeZone("UTC")

            val date = isoFormat.parse(ngaySinhRaw ?: return "Không rõ")
            val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            outputFormat.format(date!!)
        } catch (e: Exception) {
            "Không rõ"
        }
    }
}
