const express = require('express');
const router = express();
const config = require('../config');

//create endpoint in which psychiatrist can register a his/herself
router.post('/', (req, res) => {
    const {first_name, last_name, hospital_name, phone, pincode, state } = req.body;
    config.query(`INSERT INTO psychiatrist (first_name, last_name, hospital_name, phone, pincode, state) VALUES ('${first_name}', '${last_name}', '${hospital_name}', '${phone}', '${pincode}', '${state}')`, (err, result) => {
        if (err) res.send(err);
        res.json(result);
    });
});

//create endpoint in get all psychiatrist from database
router.get('/', (req, res) => {
    config.query(`SELECT * FROM psychiatrist`, (err, result) => {
        if (err) res.send(err);
        res.json(result);
    });
});

module.exports = router;