const mysql = require("mysql2");

const db = mysql.createConnection({
    host: "localhost",
    user: "root",
    password: "3006",
    database: "flight_booking"
});

db.connect(err => {
    if (err) {
        console.error("❌ Kết nối MySQL thất bại:", err.message);
    } else {
        console.log("✅ Đã kết nối MySQL thành công!");
    }
});

module.exports = db;
