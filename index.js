const express = require('express');
const app = express();
const bodyParser = require('body-parser');

// create connection to mysql database

app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));

//API endpont 1 : where all Operation on the patient table will be done
const patientRouter = require('./apis/patient');
app.use('/patient', patientRouter);

// //API endpont 1 : we will get all the patients from the database
//  respective to the id of the psychiatrist
const allPatients = require('./apis/allpatients');
app.use('/allpatients', allPatients);

const register = require('./apis/register');
app.use('/register', register);

//fetch count of each psychatrist patients
const count = require('./apis/count');
app.use('/count', count);
app.use(express.json());
const PORT = 3000;
app.listen(PORT, () => console.log(`App listening on port ${PORT}`));