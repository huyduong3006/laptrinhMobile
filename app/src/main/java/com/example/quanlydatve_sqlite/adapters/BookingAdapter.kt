package com.example.quanlydatve_sqlite.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.databinding.ItemVeChuyenBayBinding
import com.example.quanlydatve_sqlite.models.Booking
import com.example.quanlydatve_sqlite.utils.FormatDate

class BookingAdapter(private val bookings: List<Booking>) : RecyclerView.Adapter<BookingAdapter.BookingViewHolder>() {

    inner class BookingViewHolder(private val binding: ItemVeChuyenBayBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(booking: Booking) {
            binding.Name.text = "${booking.tu} → ${booking.den}"
            binding.NgayDi.text = "Đi: ${FormatDate.formatNgay(booking.ngayDi)}"
            binding.NgayVe.text = "Về: ${FormatDate.formatNgay(booking.ngayVe)}"
            binding.LoaiVe.text = "Loại: ${booking.ticketType}"
            binding.SoLuong.text = "Số lượng: ${booking.quantity}"
            binding.PhuongThuc.text = "HTTT: ${booking.paymentMethod}"
            binding.ThanhTien.text = "Tổng tiền: %,d VNĐ".format(booking.totalPrice.toInt())

            val destination = booking.den.lowercase().replace(" ", "")

            val imageRes = when (destination) {
                "phuquoc" -> R.drawable.phuquoc
                "dalat" -> R.drawable.dalat
                "nhatrang" -> R.drawable.nhatrang
                "danang" -> R.drawable.danang
                "hanoi" -> R.drawable.hanoi
                "vungtau" -> R.drawable.vungtau
                "quangngai" -> R.drawable.quangngai
                else -> R.drawable.hanoi
            }

            binding.Image.setImageResource(imageRes)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookingViewHolder {
        val binding = ItemVeChuyenBayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookingViewHolder, position: Int) {
        holder.bind(bookings[position])
    }

    override fun getItemCount(): Int = bookings.size
}
