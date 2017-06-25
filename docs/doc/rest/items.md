<!DOCTYPE html>
<html>
	<head>
		<meta charset='UTF-8'>
		<link rel='stylesheet' href='css/combined-style.css'>
		<title>Match and Trade - Documentation</title>
	</head>
<body>
Items
=================
_Resource_ for Items. Items are included in a [trade membership][1] to indicate which items a [user][2] wants to exchange in a [trade][3].  

#### Operations
* `POST rest/v1/trade-memberships/{tradeMembershipId}/items/`
* `PUT rest/v1/trade-memberships/{tradeMembershipId}/items/`
* `GET rest/v1/trade-memberships/{tradeMembershipId}/items/{itemId}`
* `GET rest/v1/trade-memberships/{tradeMembershipId}/items?{queryParameters}`
* `GET rest/v1/trade-memberships/{tradeMembershipId}/items/`
* `DELETE rest/v1/trade-memberships/{tradeMembershipId}/items/{itemId}`

#### Authorization
* Only authenticated consumers.
* Can only POST to [trade-memberships][1] associated to the [authenticated][4] `userId`.

#### Resource
| Field Name | Rules | Description |
| ---------- | ----- | ----------- |
itemId | read-only | Id associated with this resource
name | read-write, mandatory, unique, 3 min length, 150 max length, unique (case insensitive) within the [TradeMembership][1] | Name of this item

##### Query Parameters
| Field Name | Description |
| ---------- | ----------- |
name | Resources matching `Item.name`
_pageSize | See [pagination][5]
_pageNumber | See [pagination][5]


#### Examples
<div class='code'>-----  Request  -----
POST http://localhost:8081/rest/v1/trade-memberships/9/items
Headers:  {Authorization: 5677-1028-6638}{Content-Type: application/json}

{
  "name" : "Pandemic Legacy: Season 1"
}

-----  Response  -----
Status:   HTTP/1.1 201 

{
  "name" : "Pandemic Legacy: Season 1",
  "itemId" : 6,
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/9/items/6"
  } ]
}
</div>

<div class='code'>-----  Request  -----
PUT http://localhost:8081/rest/v1/trade-memberships/9/items/6
Headers:  {Authorization: 5677-1028-6638}{Content-Type: application/json}

{
  "name" : "Pandemic Legacy: Season 1 After PUT"
}

-----  Response  -----
Status:   HTTP/1.1 200 

{
  "name" : "Pandemic Legacy: Season 1 After PUT",
  "itemId" : 6,
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/9/items/6"
  } ]
}
</div>

<div class='code'>-----  Request  -----
GET http://localhost:8081/rest/v1/trade-memberships/9/items/6
Headers:  {Authorization: 5677-1028-6638}{Content-Type: application/json}

-----  Response  -----
Status:   HTTP/1.1 200 

{
  "name" : "Pandemic Legacy: Season 1 After PUT",
  "itemId" : 6,
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/9/items/6"
  } ]
}
</div>

<div class='code'>-----  Request  -----
GET http://localhost:8081/rest/v1/trade-memberships/9/items?name=Pandemic%20Legacy:%20Season%201%20After%20PUT
Headers:  {Authorization: 5677-1028-6638}{Content-Type: application/json}

-----  Response  -----
Status:   HTTP/1.1 200 
Headers:  {X-Pagination-Total-Count: 1}

[ {
  "name" : "Pandemic Legacy: Season 1 After PUT",
  "itemId" : 6,
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/9/items/6"
  } ]
} ]
</div>

<div class='code'>-----  Request  -----
GET http://localhost:8081/rest/v1/trade-memberships/9/items
Headers:  {Authorization: 5677-1028-6638}{Content-Type: application/json}

-----  Response  -----
Status:   HTTP/1.1 200 
Headers:  {X-Pagination-Total-Count: 1}

[ {
  "name" : "Pandemic Legacy: Season 1 After PUT",
  "itemId" : 6,
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/9/items/6"
  } ]
} ]
</div>

[1]: trade-memberships.md
[2]: users.md
[3]: trades.md
[4]: authentications.md
[5]: ../rest-guide.md</body>
</html>
