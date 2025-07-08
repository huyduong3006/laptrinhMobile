package com.example.quanlydatve_sqlite.activities

import NguoiDung
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.networks.RetrofitClient
import com.example.quanlydatve_sqlite.networks.UpdateUserRequest
import com.example.quanlydatve_sqlite.networks.UserApi
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class EditUserActivity : AppCompatActivity() {

    private lateinit var edtHoTen: EditText
    private lateinit var edtNgaySinh: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPhone: EditText
    private lateinit var edtMatKhau: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var toolbar: Toolbar

    private lateinit var userApi: UserApi
    private var username: String? = null
    private var currentUser: NguoiDung? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_user)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        edtHoTen = findViewById(R.id.edtHoTen)
        edtNgaySinh = findViewById(R.id.edtNgaySinh)
        edtEmail = findViewById(R.id.edtEmail)
        edtPhone = findViewById(R.id.edtPhone)
        edtMatKhau = findViewById(R.id.edtMatKhau)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancle)

        userApi = RetrofitClient.instance.create(UserApi::class.java)
        username = intent.getStringExtra("username")

        if (username == null) {
            Toast.makeText(this, "Không có username", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        loadUserInfo(username!!)

        btnSave.setOnClickListener {
            saveUserInfo()
        }

        btnCancel.setOnClickListener {
            finish()
        }
    }

    private fun loadUserInfo(username: String) {
        userApi.getUserByUsername(username).enqueue(object : Callback<NguoiDung> {
            override fun onResponse(call: Call<NguoiDung>, response: Response<NguoiDung>) {
                if (response.isSuccessful) {
                    currentUser = response.body()
                    currentUser?.let { user ->
                        edtHoTen.setText(user.hoTen)

                        val formattedNgaySinh = user.ngaySinh?.let {
                            try {
                                val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                                val outputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                                val parsedDate = inputFormat.parse(it.substring(0, 10))
                                outputFormat.format(parsedDate!!)
                            } catch (e: Exception) {
                                "Không rõ"
                            }
                        } ?: "Không rõ"

                        edtNgaySinh.setText(formattedNgaySinh)
                        edtEmail.setText(user.email)
                        edtPhone.setText(user.phone)
                        edtMatKhau.setText(user.matKhau)
                    }
                } else {
                    Toast.makeText(this@EditUserActivity, "Không tải được thông tin người dùng", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NguoiDung>, t: Throwable) {
                Toast.makeText(this@EditUserActivity, "Lỗi mạng: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveUserInfo() {
        val hoTen = edtHoTen.text.toString().trim()
        val ngaySinhInput = edtNgaySinh.text.toString().trim()
        val email = edtEmail.text.toString().trim()
        val phone = edtPhone.text.toString().trim()
        val matKhau = edtMatKhau.text.toString().trim()

        if (hoTen.isEmpty() || ngaySinhInput.isEmpty() || email.isEmpty() || phone.isEmpty() || matKhau.isEmpty()) {
            Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
            return
        }

        val inputFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val ngaySinhFormatted = try {
            val date = inputFormat.parse(ngaySinhInput)
            outputFormat.format(date!!)
        } catch (e: Exception) {
            Toast.makeText(this, "Ngày sinh không đúng định dạng dd/MM/yyyy", Toast.LENGTH_SHORT).show()
            return
        }

        val updateUserRequest = UpdateUserRequest(
            hoTen = hoTen,
            ngaySinh = ngaySinhFormatted,
            email = email,
            phone = phone,
            matKhau = matKhau
        )

        username?.let {
            userApi.updateUser(it, updateUserRequest).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@EditUserActivity, "Cập nhật thành công", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this@EditUserActivity, "Cập nhật thất bại", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(this@EditUserActivity, "Lỗi mạng: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
            currentFocus!!.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }
}
