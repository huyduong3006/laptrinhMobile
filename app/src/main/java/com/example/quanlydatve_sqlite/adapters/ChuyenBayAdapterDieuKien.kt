package com.example.quanlydatve_sqlite.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlydatve_sqlite.ChuyenBay
import com.example.quanlydatve_sqlite.R
import java.text.NumberFormat
import java.util.*

class ChuyenBayAdapterDieuKien(
    private val list: List<ChuyenBay>,
    private val onItemClick: (ChuyenBay) -> Unit
) : RecyclerView.Adapter<ChuyenBayAdapterDieuKien.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgHinh: ImageView = view.findViewById(R.id.imgHinh)
        val txtTuDen: TextView = view.findViewById(R.id.txtTuDen)
        val txtNgayDi: TextView = view.findViewById(R.id.txtNgayDi)
        val txtNgayVe: TextView = view.findViewById(R.id.txtNgayVe)
        val txtGia: TextView = view.findViewById(R.id.txtGia)
        val btnDatVe: Button = view.findViewById(R.id.btnDatVe)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chuyen_bay, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val cb = list[position]
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        val giaFormatted = formatter.format(cb.giaVe)

        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        val ngayDiFormatted = try {
            val date = inputFormat.parse(cb.ngayDi.substring(0, 10))
            outputFormat.format(date!!)
        } catch (e: Exception) {
            cb.ngayDi.substring(0, 10)
        }

        val ngayVeFormatted = try {
            val date = inputFormat.parse(cb.ngayVe.substring(0, 10))
            outputFormat.format(date!!)
        } catch (e: Exception) {
            cb.ngayVe.substring(0, 10)
        }

        holder.imgHinh.setImageResource(cb.getHinhAnhResId())
        holder.txtTuDen.text = "${cb.tu} → ${cb.den}"
        holder.txtNgayDi.text = "Ngày đi: $ngayDiFormatted"
        holder.txtNgayVe.text = "Ngày về: $ngayVeFormatted"
        holder.txtGia.text = "Giá vé: $giaFormatted Đ"

        holder.btnDatVe.setOnClickListener {
            onItemClick(cb)
        }
    }

    override fun getItemCount(): Int = list.size
}
