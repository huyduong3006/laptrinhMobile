package com.example.quanlydatve_sqlite.activities

import android.content.Intent
import android.os.Bundle
import android.webkit.WebView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.models.Booking
import com.example.quanlydatve_sqlite.networks.BookingApi
import com.example.quanlydatve_sqlite.networks.RetrofitClient
import com.example.quanlydatve_sqlite.utils.FormatDate
import com.example.quanlydatve_sqlite.utils.SharedPrefHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HoaDonBooking : AppCompatActivity() {

    private var hasSentToServer = false

    private fun generateInvoiceHtml(booking: Booking): String {
        return """
        <html>
        <head>
            <style>
                body { font-family: sans-serif; padding: 16px; color: #fff; }
                .invoice-container { background-color: #1A1A1A; border-radius: 8px; padding: 16px; }
                .invoice-title { font-size: 20px; font-weight: bold; margin-bottom: 16px; }
                .invoice-row { margin-bottom: 8px; font-size: 16px; }
                .highlight { color: #ff9800; font-weight: bold; font-size: 18px; }
            </style>
        </head>
        <body>
            <div class="invoice-container">
                <div class="invoice-title">✈️ Chi tiết hóa đơn đặt vé</div>
                <div class="invoice-row">📍 <b>Từ:</b> ${booking.tu}</div>
                <div class="invoice-row">📍 <b>Đến:</b> ${booking.den}</div>
                <div class="invoice-row">🗓️ <b>Ngày đi:</b> ${FormatDate.formatNgay(booking.ngayDi)}</div>
                <div class="invoice-row">🗓️ <b>Ngày về:</b> ${FormatDate.formatNgay(booking.ngayVe)}</div>
                <div class="invoice-row">🎟️ <b>Loại vé:</b> ${booking.ticketType}</div>
                <div class="invoice-row">🔢 <b>Số lượng:</b> ${booking.quantity}</div>
                <div class="invoice-row">💳 <b>Thanh toán:</b> ${booking.paymentMethod}</div>
                <hr style="margin-top:20px; margin-bottom:16px; border: 0.5px solid #888888;">
                <div class="invoice-row highlight">💰 Tổng tiền: ${"%,.0f VNĐ".format(booking.totalPrice).replace(',', '.')}</div>
            </div>
        </body>
        </html>
        """.trimIndent()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hoa_don)

        val btnTrangChu = findViewById<Button>(R.id.btnTrangchu)
        val btnDownload = findViewById<Button>(R.id.btnDownload)
        val invoiceWebView = findViewById<WebView>(R.id.invoiceText)

        val booking = intent.getSerializableExtra("BOOKING") as? Booking
        if (booking == null) {
            Toast.makeText(this, "Không nhận được dữ liệu đặt vé", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        invoiceWebView.settings.javaScriptEnabled = false
        invoiceWebView.setBackgroundColor(0x00000000)
        invoiceWebView.loadDataWithBaseURL(null, generateInvoiceHtml(booking), "text/html", "utf-8", null)

        btnTrangChu.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        btnDownload.setOnClickListener {
            Toast.makeText(this, "Tính năng tải xuống đang được phát triển", Toast.LENGTH_SHORT).show()
        }
    }


}
