const express = require("express");
const router = express.Router();
const {
    getAllFlights,
    searchFlights,
    searchFlightsByAddress,
    addFlight,
    updateFlight,
    deleteFlight,
    getFeaturedFlights
} = require("../controllers/chuyenBayController");

router.get("/search", searchFlights);   
router.get("/search-by-address", searchFlightsByAddress);        // ✅ đặt TRƯỚC /:id
     // ✅ đặt TRƯỚC /:id
router.get("/featured", getFeaturedFlights); // ✅ cũng TRƯỚC

router.get("/", getAllFlights);
router.post("/", addFlight);
router.put("/:id", updateFlight);
router.delete("/:id", deleteFlight);

module.exports = router;
