const db = require("../config/db");

exports.getUserByUsername = (req, res) => {
    const username = req.params.username;
    db.query("SELECT * FROM NguoiDung WHERE tenDangNhap = ?", [username], (err, results) => {
        if (err) return res.status(500).send("Lỗi truy vấn");
        if (results.length === 0) return res.status(404).send("Không tìm thấy người dùng");
        res.json(results[0]);
    });
};

exports.updateUser = (req, res) => {
    const username = req.params.username;
    const { hoTen, ngaySinh, email, phone, matKhau } = req.body;
    const sql = "UPDATE NguoiDung SET hoTen = ?, ngaySinh = ?, email = ?, phone = ?, matKhau = ? WHERE tenDangNhap = ?";
    db.query(sql, [hoTen, ngaySinh, email, phone, matKhau, username], (err, result) => {
        if (err) return res.status(500).send("Lỗi cập nhật thông tin");
        res.send("Cập nhật thành công");
    });
};
