const db = require("../config/db");

exports.getAllFlights = (req, res) => {
    console.log("🔥 ĐÃ GỌI getAllFlights()");
    db.query("SELECT * FROM ChuyenBay", (err, results) => {
        if (err) return res.status(500).send("Lỗi truy vấn");
        res.json(results);
    });
};

exports.searchFlights = (req, res) => {
    console.log("GET /search", req.query); 

    const { tu, den, ngayDi, ngayVe } = req.query;
    console.log(`Chuyến bay tìm kiếm: Từ ${tu} đến ${den}, ngày đi: ${ngayDi}${ngayVe ? `, ngày về: ${ngayVe}` : ""}`);

    let sql = "SELECT * FROM ChuyenBay WHERE tu = ? AND den = ? AND ngayDi = ?";
    const params = [tu, den, ngayDi];

    if (ngayVe) {
        sql += " AND ngayVe = ?";
        params.push(ngayVe);
    }

    db.query(sql, params, (err, results) => {
        if (err) return res.status(500).send("Lỗi truy vấn");
        res.json(results);
    });
};

exports.addFlight = (req, res) => {
    const { tu, den, ngayDi, ngayVe, gia, hinhAnh } = req.body;
    console.log(`Chuyến bay mới: Từ ${tu} đến ${den}, ngày đi: ${ngayDi}, ngày về: ${ngayVe || "Không có"}, giá: ${gia}, hình ảnh: ${hinhAnh}`);
    db.query("INSERT INTO ChuyenBay SET ?", { tu, den, ngayDi, ngayVe, gia, hinhAnh }, (err) => {
        if (err) return res.status(500).send("Lỗi thêm chuyến bay");
        res.send("Thêm chuyến bay thành công");
    });
};

exports.updateFlight = (req, res) => {
    const { id } = req.params;
    const { tu, den, ngayDi, ngayVe, gia, hinhAnh } = req.body;
    db.query(
        "UPDATE ChuyenBay SET tu=?, den=?, ngayDi=?, ngayVe=?, gia=?, hinhAnh=? WHERE id=?",
        [tu, den, ngayDi, ngayVe, gia, hinhAnh, id],
        (err) => {
            if (err) return res.status(500).send("Lỗi cập nhật chuyến bay");
            res.send("Cập nhật thành công");
        }
    );
};

exports.deleteFlight = (req, res) => {
    const { id } = req.params;
    db.query("DELETE FROM ChuyenBay WHERE id=?", [id], (err) => {
        if (err) return res.status(500).send("Lỗi xoá chuyến bay");
        res.send("Xoá thành công");
    });
};

exports.getFeaturedFlights = (req, res) => {
    db.query("SELECT * FROM ChuyenBay ORDER BY RAND() LIMIT 5", (err, results) => {
        if (err) return res.status(500).json({ message: "Lỗi server", error: err });
        res.json(results);
    });
};

exports.searchFlightsByAddress = (req, res) => {
   console.log("GET /search-by-address", req.query);

    const { tu, den } = req.query;

    if (!tu || !den) {
        return res.status(400).send("Thiếu thông tin nơi đi hoặc nơi đến");
    }

    const sql = "SELECT * FROM ChuyenBay WHERE tu = ? AND den = ?";
    const params = [tu, den];

    db.query(sql, params, (err, results) => {
        if (err) {
            console.error("❌ Lỗi truy vấn chuyến bay theo nơi đi/đến:", err);
            return res.status(500).send("Lỗi truy vấn chuyến bay theo nơi đi/đến");
        }
        res.json(results);
    });
};
