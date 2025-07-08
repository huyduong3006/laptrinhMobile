const express = require("express");
const router = express.Router();
const bookingController = require("../controllers/bookingController");

router.post("/", bookingController.createBooking);
router.get("/", bookingController.getAllBookings);
router.get("/user/:username", bookingController.getBookingsByUsername);
router.get("/latest/:userId", bookingController.getLatestBookingByUserId);
router.get("/invoice/booking/:bookingId", bookingController.getInvoiceByBookingId);
router.get("/invoices/user/:userId", bookingController.getInvoicesByUserId);
router.put("/update-payment-info", bookingController.updateInvoicePaymentInfo); 

module.exports = router;
