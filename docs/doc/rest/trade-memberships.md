<!DOCTYPE html>
<html>
	<head>
		<meta charset='UTF-8'>
		<link rel='stylesheet' href='css/combined-style.css'>
		<title>Match and Trade - Documentation</title>
	</head>
<body>
Trade Memberships
=================
_Resource_ for Trade Memberships. [Users][1] who want to subscribe to a [trade][2] need create a trade membership.  

#### Operations
* `POST rest/v1/trade-memberships/`
* `GET rest/v1/trade-memberships/{tradeMembershipId}`
* `GET rest/v1/trade-memberships?{queryParameters}`
* `GET rest/v1/trade-memberships/`
* `DELETE rest/v1/trade-memberships/{tradeMembershipId}`

#### Authorization
* Only authenticated clients.

#### Resource
| Field Name | Rules | Description |
| ---------- | ----- | ----------- |
userId | read-write, mandatory | Must belong to an existing [user][1]
tradeId | read-write, mandatory | Must belong to an existing [trade][2]
tradeMembershipId | read-only | Id associated with this resource

##### Query Parameters
| Field Name | Description |
| ---------- | ----------- |
userId | Resources matching `TradeMembership.userId`
tradeId | Resources matching `TradeMembership.tradeId`
_pageSize | See [pagination][3]
_pageNumber | See [pagination][3]

#### Rules
* Cannot delete the owner of an existing [trade][2]
* Cannot create more than one trade membership for the same [trade][2]

#### Examples
<div class='code'>-----  Request  -----
POST http://localhost:8081/rest/v1/trade-memberships/
Headers:  {Authorization: 791-3430-897}{Content-Type: application/json}

{
  "tradeId" : 3,
  "userId" : 5
}

-----  Response  -----
Status:   HTTP/1.1 201 

{
  "tradeMembershipId" : 7,
  "userId" : 5,
  "tradeId" : 3,
  "type" : "MEMBER",
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/3"
  } ]
}
</div>

<div class='code'>-----  Request  -----
GET http://localhost:8081/rest/v1/trade-memberships/7
Headers:  {Authorization: 791-3430-897}{Content-Type: application/json}

-----  Response  -----
Status:   HTTP/1.1 200 

{
  "tradeMembershipId" : 7,
  "userId" : 5,
  "tradeId" : 3,
  "type" : "MEMBER",
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/3"
  } ]
}
</div>

<div class='code'>-----  Request  -----
GET http://localhost:8081/rest/v1/trade-memberships/?userId=5&tradeId=3&_pageNumber=1&_pageSize=10
Headers:  {Authorization: 791-3430-897}{Content-Type: application/json}

-----  Response  -----
Status:   HTTP/1.1 200 
Headers:  {X-Pagination-Total-Count: 1}

[ {
  "tradeMembershipId" : 7,
  "userId" : 5,
  "tradeId" : 3,
  "type" : "MEMBER",
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/3"
  } ]
} ]
</div>

<div class='code'>-----  Request  -----
GET http://localhost:8081/rest/v1/trade-memberships/
Headers:  {Authorization: 791-3430-897}{Content-Type: application/json}

-----  Response  -----
Status:   HTTP/1.1 200 
Headers:  {X-Pagination-Total-Count: 5}

[ {
  "tradeMembershipId" : 1,
  "userId" : 1,
  "tradeId" : 1,
  "type" : "OWNER",
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/1"
  } ]
}, {
  "tradeMembershipId" : 2,
  "userId" : 2,
  "tradeId" : 1,
  "type" : "MEMBER",
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/1"
  } ]
}, {
  "tradeMembershipId" : 3,
  "userId" : null,
  "tradeId" : 1,
  "type" : "OWNER",
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/1"
  } ]
}, {
  "tradeMembershipId" : 6,
  "userId" : 4,
  "tradeId" : 3,
  "type" : "OWNER",
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/3"
  } ]
}, {
  "tradeMembershipId" : 7,
  "userId" : 5,
  "tradeId" : 3,
  "type" : "MEMBER",
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/3"
  } ]
} ]
</div>

<div class='code'>-----  Request  -----
DELETE http://localhost:8081/rest/v1/trade-memberships/7
Headers:  {Authorization: 791-3430-897}{Content-Type: application/json}

-----  Response  -----
Status:   HTTP/1.1 204 

</div>

[1]: users.md
[2]: trades.md
[3]: ../rest-guide.md</body>
</html>
