import com.google.gson.annotations.SerializedName

data class NguoiDung(
    val id: Int,
    @SerializedName("tenDangNhap")
    val username: String,
    val matKhau: String,
    val hoTen: String?,
    val ngaySinh: String?,
    val email: String?,
    val phone: String?,
    val role: String?
)
