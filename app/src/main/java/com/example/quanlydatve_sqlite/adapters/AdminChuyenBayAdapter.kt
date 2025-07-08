package com.example.quanlydatve_sqlite.adapters

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quanlydatve_sqlite.ChuyenBay
import com.example.quanlydatve_sqlite.R
import com.example.quanlydatve_sqlite.activities.EditChuyenBayActivity
import com.example.quanlydatve_sqlite.databinding.ItemAdminChuyenbayBinding
import com.example.quanlydatve_sqlite.networks.ChuyenBayApi
import com.example.quanlydatve_sqlite.networks.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.awaitResponse
import java.io.File
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*

class AdminChuyenBayAdapter(
    private val danhSach: MutableList<ChuyenBay>,
    private val onFlightChanged: () -> Unit
) : RecyclerView.Adapter<AdminChuyenBayAdapter.ViewHolder>() {

    private val api: ChuyenBayApi = RetrofitClient.instance.create(ChuyenBayApi::class.java)

    inner class ViewHolder(val binding: ItemAdminChuyenbayBinding) :
        RecyclerView.ViewHolder(binding.root)

    private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAdminChuyenbayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cb = danhSach[position]
        val context = holder.itemView.context

        with(holder.binding) {
            tenChuyenBay.text = "${cb.tu} → ${cb.den}"
            NgayDi.text = formatDate(cb.ngayDi)
            NgayVe.text = formatDate(cb.ngayVe)
            Gia.text = "${DecimalFormat("#,###").format(cb.giaVe)} VNĐ"

            val imageSource = when {
                cb.hinhAnh.startsWith("http") -> cb.hinhAnh
                File(cb.hinhAnh).exists() -> File(cb.hinhAnh)
                else -> {
                    val resId = context.resources.getIdentifier(cb.hinhAnh, "drawable", context.packageName)
                    resId.takeIf { it != 0 } ?: R.drawable.background_airplane
                }
            }
            Glide.with(context).load(imageSource).into(hinhChuyenBay)

            btnXoa.setOnClickListener {
                AlertDialog.Builder(context)
                    .setTitle("Xác nhận xoá")
                    .setMessage("Bạn có chắc muốn xoá chuyến bay này không?")
                    .setPositiveButton("Xoá") { _, _ ->
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val response = api.deleteFlight(cb.id).awaitResponse()
                                if (response.isSuccessful) {
                                    (context as? android.app.Activity)?.runOnUiThread {
                                        danhSach.removeAt(position)
                                        notifyItemRemoved(position)
                                        Toast.makeText(context, "Đã xoá chuyến bay", Toast.LENGTH_SHORT).show()
                                        onFlightChanged()
                                    }
                                } else {
                                    showToast(context, "Xoá thất bại: ${response.code()}")
                                }
                            } catch (e: Exception) {
                                showToast(context, "Lỗi xoá: ${e.message}")
                            }
                        }
                    }
                    .setNegativeButton("Huỷ", null)
                    .show()
            }

            btnSua.setOnClickListener {
                val intent = Intent(context, EditChuyenBayActivity::class.java).apply {
                    putExtra("id", cb.id)
                    putExtra("tu", cb.tu)
                    putExtra("den", cb.den)
                    putExtra("ngayDi", cb.ngayDi)
                    putExtra("ngayVe", cb.ngayVe)
                    putExtra("giaVe", cb.giaVe)
                    putExtra("hinhAnh", cb.hinhAnh)                }
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = danhSach.size

    private fun formatDate(dateStr: String): String {
        return try {
            val date = inputDateFormat.parse(dateStr)
            outputDateFormat.format(date!!)
        } catch (e: Exception) {
            dateStr
        }
    }

    private fun showToast(context: android.content.Context, message: String) {
        (context as? android.app.Activity)?.runOnUiThread {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
}
