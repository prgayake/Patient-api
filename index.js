const express = require('express');
const app = express();
const bodyParser = require('body-parser');

// create connection to mysql database

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({
    extended: true
}));

const patientRouter = require('./routes/patient');
app.use('/patient', patientRouter);

const allPatients = require('./routes/allpatients');
app.use('/allpatients', allPatients);

const PORT = 3000;
app.listen(PORT, () => console.log(`App listening on port ${PORT}`));