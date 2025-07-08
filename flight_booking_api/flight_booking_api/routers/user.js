const express = require("express");
const router = express.Router();
const { getUserByUsername, updateUser } = require("../controllers/userController");

router.get("/:username", getUserByUsername);
router.put("/:username", updateUser);


module.exports = router;
