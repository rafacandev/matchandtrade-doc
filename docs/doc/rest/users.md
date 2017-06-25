<!DOCTYPE html>
<html>
	<head>
		<meta charset='UTF-8'>
		<link rel='stylesheet' href='css/combined-style.css'>
		<title>Match and Trade - Documentation</title>
	</head>
<body>
Users
======
_Resource_ for users. Typically, users are created in the application on the first time they authenticate in the system. See [Authenticate][1] for more details.

#### Operations
* `PUT rest/v1/users/{userId}`
* `GET rest/v1/users/{userId}`

#### Authorization
* Only authenticated clients.
* Clients can only get the resource associated with their own authenticated session. In other words, firstly the client needs to get the `userId` from [authentications][2] and pass it in the URL. 

#### Resource
| Field Name | Rules | Description |
| ---------- | ----- | ----------- |
email | read-only | The user's email address, this value is assigned by the Authentication Authority during the sign-up process
name | read-write, 150 max length | The user's name this value is firstly assigned by the Authentication Authority during the sign-up process
userId | read-only | Id associated with this resource

#### Examples
<div class='code'>-----  Request  -----
PUT http://localhost:8081/rest/v1/users/4
Headers:  {Authorization: 9396-644-7794}{Content-Type: application/json}

{
  "email" : "1498432105836@test.com",
  "name" : "Scott Summers"
}

-----  Response  -----
Status:   HTTP/1.1 200 

{
  "email" : "1498432105836@test.com",
  "name" : "Scott Summers",
  "userId" : 4,
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/users/4"
  } ]
}
</div>

<div class='code'>-----  Request  -----
GET http://localhost:8081/rest/v1/users/4
Headers:  {Authorization: 9396-644-7794}{Content-Type: application/json}

-----  Response  -----
Status:   HTTP/1.1 200 

{
  "email" : "1498432105836@test.com",
  "name" : "Scott Summers",
  "userId" : 4,
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/users/4"
  } ]
}
</div>

[1]: authenticate.md
[2]: authentications.md</body>
</html>
