const mysql = require("mysql2/promise");

const db = mysql.createPool({
    host: "localhost",
    user: "root",
    password: "3006",
    database: "flight_booking",
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0
});

// ✅ Có thể test kết nối bằng một câu query đơn giản
db.query('SELECT 1')
    .then(() => {
        console.log("✅ Đã kết nối MySQL thành công!");
    })
    .catch(err => {
        console.error("❌ Kết nối MySQL thất bại:", err.message);
    });

module.exports = db;
