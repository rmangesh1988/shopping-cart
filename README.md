#hardware-store-api

<b>Build :</b><br />
mvn clean install

<b>Start MySQL : </b><br />
docker-compose up -d

<b>To start the application run : </b>  
HardwareStoreApiApplication.java

Spring security and JWT is used to facilitate authentication and authorization.
Hit the below uri to 1st authenticate the user,

http://localhost:8080/api/v1/authenticate

Two users will be created at start up : <br />
<b>Admin user</b> : <br />
username/e-mail : a@a.com<br />
password : 123

<b>Customer user</b> : <br />
username/e-mail : b@b.com<br />
password : 456

For one of the users you try to authenticate with, you will receive a JWT token in the header "<b>access-token</b>"

You will have to use the token in the Authorization header as show below, <br /> 
key           : value<br /> 
Authorization : Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhQGEuY29tIiwicm9sZXMiOlsiQURNSU4iXSwiaXNzIjoiaGFyZHdhcmUtc3RvcmUiLCJleHAiOjE2Mjk3MzM0NzB9.qOveRhYE5GtrIMJg6UhSw1y4Dkv6fwIXirN8jep-Pjs
<br/>
<br/>
To get and overview of the api you can check out the swagger ui after starting the server at the below link,<br/>
http://localhost:8080/swagger-ui.html
 


<b>Notes</b> : <br/>
Tests are extremely crucial for every application, but here for the purposes of assignment/demo I have added repository, service and resource levels tests for some of the resources.<br/>
Test for other resources would be in a similar fashion.
<br/>
<br/>
Functionality wise I have tried to cover everything and would discuss more in the interview :)
<br/>


