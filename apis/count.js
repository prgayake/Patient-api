const express = require('express');
const router = express();
const config = require('../config');

//Create endpoint for the count of patient for each pyschiatrist
router.get('/', (req, res) => {
    config.query('SELECT count(*) as count,psychiatrist_id,H.first_name, H.hospital_name  FROM patient  P join psychiatrist H on P.psychiatrist_id = H.id group by psychiatrist_id, H.first_name, H.hospital_name;', (err, result) => {
        if (err) res.send(err);
        res.json(result);
    });
});


module.exports = router;