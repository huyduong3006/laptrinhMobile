package com.example.quanlydatve_sqlite.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.quanlydatve_sqlite.ChuyenBay
import com.example.quanlydatve_sqlite.R
import java.text.NumberFormat
import java.util.Locale
class ChuyenBayAdapter(
    private val list: List<ChuyenBay>,
    private val onItemClick: (ChuyenBay) -> Unit
) : RecyclerView.Adapter<ChuyenBayAdapter.ChuyenBayViewHolder>() {

    inner class ChuyenBayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imgHinh: ImageView = itemView.findViewById(R.id.Image)
        val txtName: TextView = itemView.findViewById(R.id.Name)
        val txtGia: TextView = itemView.findViewById(R.id.Gia)
        val txtDetail: TextView = itemView.findViewById(R.id.detailDescriptionTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChuyenBayViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_flight, parent, false)
        return ChuyenBayViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChuyenBayViewHolder, position: Int) {
        val cb = list[position]
        val formatter = NumberFormat.getInstance(Locale("vi", "VN"))
        val giaFormatted = formatter.format(cb.giaVe)
        holder.imgHinh.setImageResource(cb.getHinhAnhResId())
        holder.txtName.text = "${cb.tu} → ${cb.den}"
        holder.txtGia.text = "%,.0f VNĐ".format(cb.giaVe)
        holder.txtGia.text = "$giaFormatted VNĐ"
        holder.txtDetail.text = "Ngày đi: ${cb.ngayDi} | Ngày về: ${cb.ngayVe}"

        holder.itemView.setOnClickListener {
            onItemClick(cb)
        }
    }

    override fun getItemCount(): Int = list.size
}
