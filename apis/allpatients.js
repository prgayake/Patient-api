const express = require('express');
const router = express();
const config = require('../config');

// create a endpoint for /patient which will return all patients in the database
router.get('/:id', (req, res) => {
    config.query(`SELECT id,name,address,email,phone,password FROM patient where psychiatrist_id =${req.params.id}`, (err, result) => {
        if (err) throw err;
        res.json(result);
    });
});

module.exports = router;