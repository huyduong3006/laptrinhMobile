const db = require("../config/db");

exports.login = (req, res) => {
    const { tenDangNhap, matKhau } = req.body;

    if (!tenDangNhap || !matKhau) {
        return res.status(400).json({ message: "Thiếu thông tin đăng nhập" });
    }

    const sql = "SELECT tenDangNhap, role FROM NguoiDung WHERE tenDangNhap = ? AND matKhau = ?";
    db.query(sql, [tenDangNhap, matKhau], (err, results) => {
        if (err) return res.status(500).json({ message: "Lỗi server" });

        if (results.length > 0) {
            const { tenDangNhap, role } = results[0];

            console.log(`🔐 Đăng nhập thành công: ${tenDangNhap} - Vai trò: ${role}`);

            return res.status(200).json({ tenDangNhap, role });
        }

        return res.status(401).json({ message: "Sai tài khoản hoặc mật khẩu" });
    });
};

exports.register = (req, res) => {
    const { tenDangNhap, matKhau, hoTen, ngaySinh, email, phone } = req.body;
    if (!tenDangNhap || !matKhau || !hoTen || !ngaySinh) {
        return res.status(400).send("Thiếu thông tin đăng ký");
    }

    const checkUser = "SELECT * FROM NguoiDung WHERE tenDangNhap = ?";
    db.query(checkUser, [tenDangNhap], (err, results) => {
        if (results.length > 0) {
            return res.status(409).send("Tên đăng nhập đã tồn tại");
        }

        const insertUser = "INSERT INTO NguoiDung SET ?";
        const newUser = { tenDangNhap, matKhau, hoTen, ngaySinh, email, phone };

        db.query(insertUser, newUser, (err, result) => {
            if (err) return res.status(500).send("Lỗi khi đăng ký");
        res.status(200).json({
        message: "Đăng ký thành công",
        user: {
            tenDangNhap,
            hoTen,
            email,
            phone,
            ngaySinh,
        }
        });
        });
    });
};
exports.logout = (req, res) => {
    return res.status(200).send("Đăng xuất thành công");
};
