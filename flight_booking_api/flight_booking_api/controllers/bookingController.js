const pool = require("../db"); // mysql2/promise connection

const formatDateTime = (isoString) => {
  if (!isoString) {
    throw new Error("Ngày không hợp lệ: " + isoString);
  }

  const date = new Date(isoString);
  if (isNaN(date.getTime())) {
    throw new Error("Không thể phân tích ngày: " + isoString);
  }

  const offsetDate = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
  return offsetDate.toISOString().slice(0, 19).replace('T', ' ');
};
// Tạo vé chuyến bay (Booking)
exports.createBooking = async (req, res) => {
    console.log("📥 Body nhận được:", req.body);
const {
  userId,
  tu,
  den,
  ngayDi,
  ngayVe,
  quantity,
  ticketType,
  price,
  tax,
  totalPrice,
  paymentMethod,
  tenNganHang,
  soTaiKhoan,
  noiDung
} = req.body;
  console.log("🔥 Thông tin nhận được khi tạo booking:", req.body);
  console.log("👉 ngayDi =", ngayDi);
  console.log("👉 ngayVe =", ngayVe);

  try {
    const ngayDiFormatted = formatDateTime(ngayDi);
    const ngayVeFormatted = formatDateTime(ngayVe);

    const [result] = await pool.query(
      `INSERT INTO Booking
        (userId, tu, den, ngayDi, ngayVe, quantity, ticketType, price, tax, totalPrice, paymentMethod)
      VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)`,
      [userId, tu, den, ngayDiFormatted, ngayVeFormatted, quantity, ticketType, price, tax, totalPrice, paymentMethod]
    );
    const bookingId = result.insertId;
    if (!bookingId) {
      return res.status(500).json({ message: "Tạo booking thất bại" });
    }

    const [rows] = await pool.query(`SELECT * FROM Booking WHERE id = ?`, [bookingId]);
    if (rows.length === 0) {
      return res.status(500).json({ message: "Không tìm thấy booking sau khi thêm" });
    }
    res.status(201).json(rows[0]);
  } catch (err) {
    console.error("Lỗi tạo booking:", err);
    res.status(500).json({ message: "Lỗi server", error: err });
  }
};

// --- Lấy toàn bộ booking ---
exports.getAllBookings = async (req, res) => {
  try {
    const [rows] = await pool.query("SELECT * FROM Booking ORDER BY createdAt DESC");
    res.json(rows);
  } catch (err) {
    console.error("Lỗi getAllBookings:", err);
    res.status(500).json({ message: "Lỗi server", error: err });
  }
};

// --- Lấy booking theo username ---
exports.getBookingsByUsername = async (req, res) => {
  const { username } = req.params;
  try {
    const [rows] = await pool.query(
      `SELECT b.* FROM Booking b
       JOIN Users u ON b.userId = u.id
       WHERE u.username = ? ORDER BY b.createdAt DESC`,
      [username]
    );
    res.json(rows);
  } catch (err) {
    console.error("Lỗi getBookingsByUsername:", err);
    res.status(500).json({ message: "Lỗi server", error: err });
  }
};

// --- Lấy booking mới nhất theo userId ---
exports.getLatestBookingByUserId = async (req, res) => {
  const { userId } = req.params;

  try {
    const [rows] = await pool.query(
      `SELECT * FROM Booking 
       WHERE userId = ? 
       ORDER BY id DESC 
       LIMIT 1`,
      [userId]
    );

    if (rows.length === 0) {
      return res.status(404).json({ message: "Không tìm thấy booking nào cho user này" });
    }

    res.status(200).json(rows[0]);
  } catch (err) {
    console.error("Lỗi getLatestBookingByUserId:", err);
    res.status(500).json({ message: "Lỗi server", error: err });
  }
};

// --- Lấy hóa đơn theo bookingId (booking + invoice info) ---
exports.getInvoiceByBookingId = async (req, res) => {
  const { bookingId } = req.params;

  try {
    const [rows] = await pool.query(
      `SELECT b.*, h.id AS invoiceId, h.createdAt AS invoiceDate
       FROM Booking b
       LEFT JOIN HoaDon h ON h.bookingId = b.id
       WHERE b.id = ?`,
      [bookingId]
    );

    if (rows.length === 0) {
      return res.status(404).json({ message: "Không tìm thấy hóa đơn cho booking này" });
    }

    res.status(200).json(rows[0]);
  } catch (err) {
    console.error("Lỗi lấy hóa đơn theo bookingId:", err);
    res.status(500).json({ message: "Lỗi server", error: err });
  }
};

// --- Lấy tất cả hóa đơn của userId (có thể dùng để xem lịch sử hóa đơn) ---
exports.getInvoicesByUserId = async (req, res) => {
  const { userId } = req.params;

  try {
    const [rows] = await pool.query(
      `SELECT b.*, h.id AS invoiceId, h.createdAt AS invoiceDate
       FROM Booking b
       LEFT JOIN HoaDon h ON h.bookingId = b.id
       WHERE b.userId = ?
       ORDER BY h.createdAt DESC`,
      [userId]
    );

    res.status(200).json(rows);
  } catch (err) {
    console.error("Lỗi lấy hóa đơn theo userId:", err);
    res.status(500).json({ message: "Lỗi server", error: err });
  }
};

const getVietnamTime = () => {
  const now = new Date();
  const vietnamTime = new Date(now.getTime() + 7 * 60 * 60 * 1000); // UTC+7
  return vietnamTime.toISOString().slice(0, 19).replace('T', ' ');
};

exports.updateInvoicePaymentInfo = async (req, res) => {
  const { bookingId, tenNganHang, soTaiKhoan, noiDung } = req.body;

  // Log dữ liệu nhận được
  console.log("==== [API] updateInvoicePaymentInfo ====");
  console.log("Body nhận được:", req.body);

  if (!bookingId || !tenNganHang || !soTaiKhoan || !noiDung) {
    console.error("❌ Thiếu thông tin thanh toán: ", req.body);
    return res.status(400).json({ message: "Thiếu thông tin thanh toán" });
  }

  const createdAt = getVietnamTime();

  try {
    console.log("Thông tin hoá đơn:", {
      bookingId, tenNganHang, soTaiKhoan, noiDung, createdAt
    });

    const [result] = await pool.query(
      `INSERT INTO HoaDon (bookingId, tenNganHang, soTaiKhoan, noiDung, createdAt)
       VALUES (?, ?, ?, ?, ?)`,
      [bookingId, tenNganHang, soTaiKhoan, noiDung, createdAt]
    );

    console.log("✔️ Lưu hóa đơn thành công:", result);

    res.status(200).json({ message: "Cập nhật hóa đơn thành công" });
  } catch (err) {
    console.error("❌ Lỗi lưu hóa đơn:", err);
    if (err && typeof err === "object") {
      console.error("❌ Thông tin lỗi chi tiết:");
      for (const key in err) {
        if (Object.hasOwnProperty.call(err, key)) {
          console.error(`    ${key}:`, err[key]);
        }
      }
    }
    res.status(500).json({ message: "Lỗi lưu thanh toán", error: err });
  }
};
