package com.example.quanlydatve_sqlite.activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.models.Booking
import com.example.quanlydatve_sqlite.models.PaymentInfoRequest
import com.example.quanlydatve_sqlite.networks.BookingApi
import com.example.quanlydatve_sqlite.networks.RetrofitClient
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable

class ThanhToanBottomSheet(private val booking: Booking) : BottomSheetDialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogTheme)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_bottomsheet_thanh_toan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val tenNganHangEditText = view.findViewById<AutoCompleteTextView>(R.id.tenNganHang)
        val soTaiKhoanEditText = view.findViewById<EditText>(R.id.soTaiKhoan)
        val soTienThanhToanEditText = view.findViewById<EditText>(R.id.soTienThanhToan)
        val noiDungEditText = view.findViewById<EditText>(R.id.noiDungChuyenTien)
        val checkCardCheckbox = view.findViewById<CheckBox>(R.id.checkCardCheckbox)
        val payButton = view.findViewById<Button>(R.id.payButton)
        val returnButton = view.findViewById<Button>(R.id.returnButton)

        val bankNames = listOf(
            "BIDV", "VIETINBANK", "VIETCOMBANK", "AGRIBANK", "ACB", "MB BANK", "TECHCOMBANK",
            "VPBANK", "TPBANK", "HDBANK", "VIB", "SHB", "SCB", "OCB", "MSB", "ABBANK",
            "LienVietPostBank", "SGB", "PG BANK", "BACABANK", "NAM A BANK", "KIENLONGBANK",
            "VIETBANK", "HSBC", "STANDARD CHARTERED", "ANZ", "UOB", "CIMB", "WOORI BANK",
            "SHINHAN BANK", "HONG LEONG BANK", "PUBLIC BANK", "IBK", "KB KOOKMIN BANK"
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, bankNames)
        tenNganHangEditText.setAdapter(adapter)

        soTienThanhToanEditText.setText(formatCurrency(booking.totalPrice))

        returnButton.setOnClickListener {
            dismiss()
        }

        payButton.setOnClickListener {
            hideKeyboard()

            val bankName = tenNganHangEditText.text.toString().trim()
            val accountNumber = soTaiKhoanEditText.text.toString().trim()
            val note = noiDungEditText.text.toString().trim()
            val confirmed = checkCardCheckbox.isChecked

            if (bankName.isEmpty() || accountNumber.isEmpty() || note.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!confirmed) {
                Toast.makeText(requireContext(), "Vui lòng xác nhận thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val paymentInfo = PaymentInfoRequest(
                bookingId = booking.id,
                tenNganHang = bankName,
                soTaiKhoan = accountNumber,
                noiDung = note
            )

            val api = RetrofitClient.instance.create(BookingApi::class.java)
            api.updatePaymentInfo(paymentInfo).enqueue(object : Callback<Void> {
                override fun onResponse(call: Call<Void>, response: Response<Void>) {
                    if (response.isSuccessful) {
                        AlertDialog.Builder(requireContext())
                            .setTitle("Đặt vé thành công")
                            .setMessage("Bạn đã đặt vé thành công với tổng số tiền: ${formatCurrency(booking.totalPrice)}.\nBạn muốn xem hóa đơn?")
                            .setPositiveButton("Xem hóa đơn") { dialog, _ ->
                                val intent = Intent(requireContext(), HoaDonBooking::class.java)
                                intent.putExtra("BOOKING", booking as Serializable)
                                startActivity(intent)
                                dialog.dismiss()
                                dismiss()
                            }
                            .setNegativeButton("Đóng") { dialog, _ ->
                                dialog.dismiss()
                                dismiss()
                            }
                            .show()
                    } else {
                        // Lấy nội dung lỗi trả về từ backend (nếu có)
                        val rawError = response.errorBody()?.string() ?: "Không rõ lỗi"
                        val errorMsg = when {
                            rawError.contains("Duplicate entry") ->
                                "Đơn đặt vé này đã được thanh toán hoặc hóa đơn đã tồn tại!"
                            else ->
                                "Lỗi lưu thanh toán: $rawError"
                        }

                        // Hiển thị Toast cho người dùng
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()

                        // Ghi logcat đầy đủ để debug
                        android.util.Log.e("PAYMENT_ERROR", "Lỗi lưu thanh toán: $rawError")
                    }
                }

                override fun onFailure(call: Call<Void>, t: Throwable) {
                    Toast.makeText(requireContext(), "Lỗi mạng: ${t.message}", Toast.LENGTH_SHORT).show()
                    android.util.Log.e("PAYMENT_ERROR", "Lỗi mạng khi gọi API thanh toán", t)
                }
            })


        }
    }

    private fun formatCurrency(amount: Double): String {
        return "%,.0f VNĐ".format(amount).replace(',', '.')
    }

    private fun hideKeyboard() {
        val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        val view = dialog?.currentFocus ?: view
        inputMethodManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}
