const express = require('express');
const router = express();
const config = require('../config');

// create a endpoint for /patient which will return all patients in the database
router.get('/', (req, res) => {
    config.query('SELECT * FROM patient', (err, result) => {
        if (err) res.send(err);
        res.json(result);
    });
});

//create endpoint insert a new patient into the database 
router.post('/', (req, res) => {

    const { name,address,email,phone,password,photo,psychiatrist_id } = req.body;
    console.log(req.body);
    config.query('INSERT INTO patient (name,address,email,phone,password,photo,psychiatrist_id) VALUES (?, ?, ?,?,?,?,?)', [name,address,email,phone,password,photo,psychiatrist_id], (err, result) => {
        if (err) res.send(err);
        res.json(result);
    });
});

router.patch('/:id', (req, res) => {
    const { name,address,email,phone,password,photo,psychiatrist_id } = req.body;
    console.log(req.body);
    config.query('UPDATE patient SET name = ?,address = ?,email = ?,phone = ?,password = ?,photo = ?,psychiatrist_id = ? WHERE id = ?', [name,address,email,phone,password,photo,psychiatrist_id,req.params.id], (err, result) => {
        if (err)  res.send(err);
        res.json(result);
    });
});

//create endpoint delete a patient from the database
router.delete('/:id', (req, res) => {
    config.query('DELETE FROM patient WHERE id = ?', [req.params.id], (err, result) => {
        if (err) res.send(err);
        res.json(result);
    });
});

module.exports = router;