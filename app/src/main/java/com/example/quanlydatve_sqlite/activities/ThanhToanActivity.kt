package com.example.quanlydatve_sqlite.activities

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.models.Booking
import java.io.Serializable

class ThanhToanActivity : AppCompatActivity() {

    private lateinit var tenNganHangEditText: AutoCompleteTextView
    private lateinit var soTaiKhoanEditText: EditText
    private lateinit var soTienThanhToanEditText: EditText
    private lateinit var noiDungEditText: EditText
    private lateinit var checkCardCheckbox: CheckBox
    private lateinit var payButton: Button
    private lateinit var returnButton: Button

    private lateinit var booking: Booking

    private val bankNames = listOf(
        "BIDV", "VIETINBANK", "VIETCOMBANK", "AGRIBANK", "ACB", "MB BANK", "TECHCOMBANK",
        "VPBANK", "TPBANK", "HDBANK", "VIB", "SHB", "SCB", "OCB", "MSB", "ABBANK",
        "LienVietPostBank", "SGB", "PG BANK", "BACABANK", "NAM A BANK", "KIENLONGBANK",
        "VIETBANK", "HSBC", "STANDARD CHARTERED", "ANZ", "UOB", "CIMB", "WOORI BANK",
        "SHINHAN BANK", "HONG LEONG BANK", "PUBLIC BANK", "IBK", "KB KOOKMIN BANK"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_thanh_toan)

        tenNganHangEditText = findViewById(R.id.tenNganHang)
        soTaiKhoanEditText = findViewById(R.id.soTaiKhoan)
        soTienThanhToanEditText = findViewById(R.id.soTienThanhToan)
        noiDungEditText = findViewById(R.id.noiDungChuyenTien)
        checkCardCheckbox = findViewById(R.id.checkCardCheckbox)
        payButton = findViewById(R.id.payButton)
        returnButton = findViewById(R.id.returnButton)

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, bankNames)
        tenNganHangEditText.setAdapter(adapter)
        tenNganHangEditText.threshold = 1

        booking = intent.getSerializableExtra("BOOKING") as? Booking
            ?: return showErrorAndFinish("Không nhận được dữ liệu đặt vé.")

        soTienThanhToanEditText.setText(formatCurrency(booking.totalPrice))

        returnButton.setOnClickListener {
            val intent = Intent(this, DatVeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
            finish()
        }


        payButton.setOnClickListener {
            val bankName = tenNganHangEditText.text.toString().trim()
            val accountNumber = soTaiKhoanEditText.text.toString().trim()
            val note = noiDungEditText.text.toString().trim()
            val confirmed = checkCardCheckbox.isChecked

            if (bankName.isEmpty() || accountNumber.isEmpty() || note.isEmpty()) {
                Toast.makeText(this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!confirmed) {
                Toast.makeText(this, "Vui lòng xác nhận thông tin đã đúng", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            showSuccessDialog()
        }
    }

    private fun showErrorAndFinish(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        finish()
    }

    private fun formatCurrency(amount: Double): String {
        return "%,.0f VNĐ".format(amount).replace(',', '.')
    }

    private fun showSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("Đặt vé thành công")
            .setMessage("Bạn đã đặt vé thành công với tổng số tiền: ${formatCurrency(booking.totalPrice)}.\nBạn muốn xem hóa đơn?")
            .setPositiveButton("Xem hóa đơn") { dialog, _ ->
                val intent = Intent(this, HoaDonBooking::class.java)
                intent.putExtra("BOOKING", booking as Serializable)
                startActivity(intent)
                dialog.dismiss()
            }
            .setNegativeButton("Đóng") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
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
