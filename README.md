# Patient-api

We have a platform where psychiatrists can register their patients through a mobile/ web portal.
Each psychiatrist belongs to a hospital. We have provided the hospital list on the last
page.(predefined list).


## Steps  For Installing / Running the Project
  1. Clone the Repository 
```
git clone https://github.com/prgayake/Patient-api
```
2. Install Node Js dependencies
```
npm install
```
3. Run Project 
```
npm start 
```

# API EndPoints 
1. patient => All crud operations 
  Link for the Patient => http://13.235.2.5:3000/patient/
  
  ![image](https://user-images.githubusercontent.com/55043418/170370808-2c411d39-6e7a-4a1e-a892-48321c7c5d5f.png)

2. allpatients
Link for the all Patient for Particular psychiatrist => http://13.235.2.5:3000/allpatients/1
![image](https://user-images.githubusercontent.com/55043418/170370940-c049fcd0-da69-44dd-b8a1-f0b6902110c5.png)

3. register
  Link for the API for psychiatrist registration => http://13.235.2.5:3000/register [Post Method]
  ![image](https://user-images.githubusercontent.com/55043418/170371566-8305f241-de69-4d78-aff2-4047a093e688.png)

4. count

# Deployment of API in AWS Ec2 Instance 
![hospital drawio (3)](https://user-images.githubusercontent.com/55043418/170373267-9e9096cc-de2f-4f01-a90c-b0cd6db52ee6.png)



