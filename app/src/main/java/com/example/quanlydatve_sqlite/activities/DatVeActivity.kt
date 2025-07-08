package com.example.quanlydatve_sqlite.activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.models.Booking
import com.example.quanlydatve_sqlite.models.BookingRequest
import com.example.quanlydatve_sqlite.networks.BookingApi
import com.example.quanlydatve_sqlite.networks.RetrofitClient
import com.example.quanlydatve_sqlite.utils.FormatDate
import com.example.quanlydatve_sqlite.utils.SharedPrefHelper
import com.google.android.material.textfield.TextInputEditText
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class DatVeActivity : AppCompatActivity() {

    private lateinit var flightRoute: TextView
    private lateinit var flightDates: TextView
    private lateinit var ticketRoute: TextView
    private lateinit var ticketDates: TextView
    private lateinit var txtGiaTien: TextView
    private lateinit var txtTax: TextView
    private lateinit var totalPrice: TextView
    private lateinit var editTextQuantity: TextInputEditText
    private lateinit var btnIncrease: ImageButton
    private lateinit var btnDecrease: ImageButton
    private lateinit var radioGroupSeatType: RadioGroup
    private lateinit var paymentGroup: RadioGroup
    private lateinit var btnDatVe: Button

    private var basePrice: Double = 0.0
    private var basePriceEffective: Double = 0.0
    private var quantity: Int = 1
    private val VAT_PERCENT = 10

    private lateinit var bookingApi: BookingApi

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dat_ve)

        // Ánh xạ view
        flightRoute = findViewById(R.id.flightRoute)
        flightDates = findViewById(R.id.flightDates)
        ticketRoute = findViewById(R.id.ticketRoute)
        ticketDates = findViewById(R.id.ticketDates)
        txtGiaTien = findViewById(R.id.txtGiaTien)
        txtTax = findViewById(R.id.txtTax)
        totalPrice = findViewById(R.id.totalPrice)
        editTextQuantity = findViewById(R.id.editTextQuantity)
        btnIncrease = findViewById(R.id.btnIncrease)
        btnDecrease = findViewById(R.id.btnDecrease)
        radioGroupSeatType = findViewById(R.id.radioGroup_seatType)
        paymentGroup = findViewById(R.id.paymentMethodGroup)
        btnDatVe = findViewById(R.id.btnDatVe)

        bookingApi = RetrofitClient.instance.create(BookingApi::class.java)

        val tu = intent.getStringExtra("EXTRA_TU") ?: ""
        val den = intent.getStringExtra("EXTRA_DEN") ?: ""
        val ngayDi = intent.getStringExtra("EXTRA_NGAY_DI") ?: ""
        val ngayVe = intent.getStringExtra("EXTRA_NGAY_VE") ?: ""
        basePrice = intent.getDoubleExtra("EXTRA_GIA", 0.0)
        basePriceEffective = basePrice
        val ngayDiFormatted = FormatDate.formatNgay(ngayDi)
        val ngayVeFormatted = FormatDate.formatNgay(ngayVe)

        flightRoute.text = "Từ $tu đến $den"
        flightDates.text = "Ngày đi: $ngayDiFormatted - Ngày về: $ngayVeFormatted"
        ticketRoute.text = "Chuyến bay: $tu - $den"
        ticketDates.text = "Ngày: $ngayDiFormatted - $ngayVeFormatted"

        editTextQuantity.setText(quantity.toString())
        updatePriceDisplay()

        btnIncrease.setOnClickListener {
            quantity++
            editTextQuantity.setText(quantity.toString())
            updatePriceDisplay()
        }

        btnDecrease.setOnClickListener {
            if (quantity > 1) {
                quantity--
                editTextQuantity.setText(quantity.toString())
                updatePriceDisplay()
            }
        }

        editTextQuantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val qty = s.toString().toIntOrNull()
                if (qty != null && qty > 0) {
                    quantity = qty
                    updatePriceDisplay()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        radioGroupSeatType.setOnCheckedChangeListener { _, checkedId ->
            basePriceEffective = when (checkedId) {
                R.id.radio_pho_thong -> basePrice
                R.id.radio_pho_thong_dac_biet -> basePrice * 1.12
                R.id.radio_thuong_gia -> basePrice * 3.0
                else -> basePrice
            }
            updatePriceDisplay()
        }

        btnDatVe.setOnClickListener {
            val role = SharedPrefHelper.getRole(this)
            if (role == "admin") {
                Toast.makeText(this, "Bạn không phải user", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (quantity < 1) {
                Toast.makeText(this, "Số lượng vé phải lớn hơn 0", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (radioGroupSeatType.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Vui lòng chọn loại vé", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (paymentGroup.checkedRadioButtonId == -1) {
                Toast.makeText(this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bookingRequest = createBookingRequestObject(tu, den, ngayDi, ngayVe)

            if (bookingRequest.ngayDi.isEmpty()) {
                Toast.makeText(this, "Ngày đi không hợp lệ!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (bookingRequest.ngayVe.isEmpty()) {
                Toast.makeText(this, "Ngày về không hợp lệ!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            Log.d("DEBUG_BOOKING", bookingRequest.toString())
            sendBookingToServer(bookingRequest)
        }

    }

    private fun updatePriceDisplay() {
        val price = basePriceEffective * quantity
        val vat = price * VAT_PERCENT / 100
        val total = price + vat

        txtGiaTien.text = formatCurrency(price)
        txtTax.text = formatCurrency(vat)
        totalPrice.text = formatCurrency(total)
    }

    private fun formatCurrency(amount: Double): String {
        return "%,.0f VNĐ".format(amount).replace(',', '.')
    }

    private fun createBookingRequestObject(tu: String, den: String, ngayDi: String, ngayVe: String): BookingRequest {
        val seatType = when (radioGroupSeatType.checkedRadioButtonId) {
            R.id.radio_pho_thong -> "Phổ thông"
            R.id.radio_pho_thong_dac_biet -> "Phổ thông đặc biệt"
            R.id.radio_thuong_gia -> "Thương gia"
            else -> "Không rõ"
        }

        val paymentMethod = when (paymentGroup.checkedRadioButtonId) {
            R.id.radioButton_Chuyenkhoan -> "Chuyển khoản"
            R.id.radioButton_Quetthe -> "Quét thẻ"
            else -> "Không rõ"
        }

        val pricePerTicket = basePriceEffective
        val totalPriceBeforeTax = pricePerTicket * quantity
        val tax = totalPriceBeforeTax * VAT_PERCENT / 100
        val total = totalPriceBeforeTax + tax

        val ngayDiIso = FormatDate.convertToISO8601(ngayDi)
        val ngayVeIso = FormatDate.convertToISO8601(ngayVe)

        return BookingRequest(
            userId = getUserIdFromSharedPrefOrSession(),
            tu = tu,
            den = den,
            ngayDi = ngayDiIso,
            ngayVe = ngayVeIso,
            quantity = quantity,
            ticketType = seatType,
            price = pricePerTicket,
            tax = tax,
            totalPrice = total,
            paymentMethod = paymentMethod
        )
    }


    private fun sendBookingToServer(bookingRequest: BookingRequest) {
        if (bookingRequest.paymentMethod == "Quét thẻ") {
            AlertDialog.Builder(this@DatVeActivity)
                .setTitle("Thông báo")
                .setMessage("Chức năng quét thẻ hiện đang được phát triển. Đơn đặt vé của bạn chưa được lưu.")
                .setPositiveButton("OK") { _, _ -> finish() }
                .setCancelable(false)
                .show()
            return
        }

        bookingApi.createBooking(bookingRequest).enqueue(object : Callback<Booking> {
            override fun onResponse(call: Call<Booking>, response: Response<Booking>) {
                if (response.isSuccessful && response.body() != null) {
                    val booking = response.body()!!

                    if (bookingRequest.paymentMethod == "Chuyển khoản") {
                        val sheet = ThanhToanBottomSheet(booking)
                        sheet.show(supportFragmentManager, "ThanhToanBottomSheet")
                    } else {
                        Toast.makeText(
                            this@DatVeActivity,
                            "Bạn đã đặt vé thành công!",
                            Toast.LENGTH_LONG
                        ).show()
                        finish()
                    }

                } else {
                    Toast.makeText(
                        this@DatVeActivity,
                        "Đặt vé thất bại: ${response.message()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<Booking>, t: Throwable) {
                Toast.makeText(this@DatVeActivity, "Lỗi mạng: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun getUserIdFromSharedPrefOrSession(): Int {
        val userId = SharedPrefHelper.getUserId(this)
        return userId ?: -1
    }

}
