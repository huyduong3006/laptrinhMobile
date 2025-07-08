const express = require("express");
const cors = require("cors");
const bodyParser = require("body-parser");
const app = express();

app.use(express.json());
app.use(cors());
app.use(bodyParser.json());

const authRoutes = require('./routers/auth');
const userRoutes = require('./routers/user');
const chuyenBayRoutes = require('./routers/chuyenbay');
const bookingRoutes = require('./routers/booking');


app.use("/api/auth", authRoutes);
app.use("/api/user", userRoutes);
app.use("/api/chuyenbay", chuyenBayRoutes); 
app.use("/api/bookings", bookingRoutes);

const PORT = 3000;
app.listen(PORT, () => {
    console.log(`Server chạy ở http://localhost:${PORT}`);
});
