<!DOCTYPE html>
<html>
	<head>
		<meta charset='UTF-8'>
		<link rel='stylesheet' href='css/combined-style.css'>
		<title>Match and Trade - Documentation</title>
	</head>
<body>
Use Cases
=========
Typical use cases usage.

#### Create a Trade
Authenticate to the application.
<div class='code'>-----  Request  -----
GET http://localhost:8081/authenticate

-----  Response  -----
Status:   HTTP/1.1 200 
Headers:  {Authorization: 4816-5640-1027}

</div>

Verify the authentication details. Note that you need to pass the `Authorization` header to all secured endpoints. 
<div class='code'>-----  Request  -----
GET http://localhost:8081/rest/v1/authentications/
Headers:  {Authorization: 4816-5640-1027}{Content-Type: application/json}

-----  Response  -----
Status:   HTTP/1.1 200 

{
  "userId" : 1,
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/authentications/"
  } ]
}
</div>

Create a [Trade][3]
<div class='code'>-----  Request  -----
POST http://localhost:8081/rest/v1/trades/
Headers:  {Authorization: 4816-5640-1027}{Content-Type: application/json}

{
  "name" : "Board games in Ottawa"
}

-----  Response  -----
Status:   HTTP/1.1 201 

{
  "name" : "Board games in Ottawa",
  "tradeId" : 1,
  "state" : "SUBMITTING_ITEMS",
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trades/1"
  } ]
}
</div>

Note that the `state` for the created [trade][3] is `SUBMITTING_ITEMS` meaning that [users][4] are allowed to subscribe and submit [items][13].

Find the [trade membership][12] associated with the _owner_.
<div class='code'>-----  Request  -----
GET http://localhost:8081/rest/v1/trade-memberships/?tradeId=1&userId=1
Headers:  {Authorization: 4816-5640-1027}{Content-Type: application/json}

-----  Response  -----
Status:   HTTP/1.1 200 
Headers:  {X-Pagination-Total-Count: 1}

[ {
  "tradeMembershipId" : 1,
  "userId" : 1,
  "tradeId" : 1,
  "type" : "OWNER",
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/1"
  } ]
} ]
</div>

Add two items to be traded by the _owner_.
<div class='code'>-----  Request  -----
POST http://localhost:8081/rest/v1/trade-memberships/1/items
Headers:  {Authorization: 4816-5640-1027}{Content-Type: application/json}

{
  "name" : "Pandemic Legacy: Season 1"
}

-----  Response  -----
Status:   HTTP/1.1 201 

{
  "name" : "Pandemic Legacy: Season 1",
  "itemId" : 1,
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/1/items/1"
  } ]
}
</div>
<div class='code'>-----  Request  -----
POST http://localhost:8081/rest/v1/trade-memberships/1/items
Headers:  {Authorization: 4816-5640-1027}{Content-Type: application/json}

{
  "name" : "Pandemic Legacy: Season 2"
}

-----  Response  -----
Status:   HTTP/1.1 201 

{
  "name" : "Pandemic Legacy: Season 2",
  "itemId" : 2,
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/1/items/2"
  } ]
}
</div>

#### A Second User Subscribes to the Trade
Authenticate with a second [user][4] which is going to become a _member_ of [trade][3] previously created.
<div class='code'>-----  Request  -----
GET http://localhost:8081/authenticate

-----  Response  -----
Status:   HTTP/1.1 200 
Headers:  {Authorization: 3233-1437-340}

</div>

Verify the authentication details for the second user. The `userId` is going to be used on the next step.
<div class='code'>-----  Request  -----
GET http://localhost:8081/rest/v1/authentications/
Headers:  {Authorization: 3233-1437-340}{Content-Type: application/json}

-----  Response  -----
Status:   HTTP/1.1 200 

{
  "userId" : 2,
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/authentications/"
  } ]
}
</div>

The second [user][4] subscribes to the [trade][3].
<div class='code'>-----  Request  -----
POST http://localhost:8081/rest/v1/trade-memberships/
Headers:  {Authorization: 3233-1437-340}{Content-Type: application/json}

{
  "tradeId" : 1,
  "userId" : 2
}

-----  Response  -----
Status:   HTTP/1.1 201 

{
  "tradeMembershipId" : 2,
  "userId" : 2,
  "tradeId" : 1,
  "type" : "MEMBER",
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/1"
  } ]
}
</div>

Add three [items][13] to be traded by the _member_.
<div class='code'>-----  Request  -----
POST http://localhost:8081/rest/v1/trade-memberships/2/items
Headers:  {Authorization: 3233-1437-340}{Content-Type: application/json}

{
  "name" : "No Thanks!"
}

-----  Response  -----
Status:   HTTP/1.1 201 

{
  "name" : "No Thanks!",
  "itemId" : 3,
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/2/items/3"
  } ]
}
</div>
<div class='code'>-----  Request  -----
POST http://localhost:8081/rest/v1/trade-memberships/2/items
Headers:  {Authorization: 3233-1437-340}{Content-Type: application/json}

{
  "name" : "Carcassonne"
}

-----  Response  -----
Status:   HTTP/1.1 201 

{
  "name" : "Carcassonne",
  "itemId" : 4,
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/2/items/4"
  } ]
}
</div>
<div class='code'>-----  Request  -----
POST http://localhost:8081/rest/v1/trade-memberships/2/items
Headers:  {Authorization: 3233-1437-340}{Content-Type: application/json}

{
  "name" : "Stone Age"
}

-----  Response  -----
Status:   HTTP/1.1 201 

{
  "name" : "Stone Age",
  "itemId" : 5,
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/2/items/5"
  } ]
}
</div>

#### Change the Trade to State to MATCHING_ITEMS
The [trade][3] _owner_ decides that is time to start the `MATCHING_ITEMS` phase by changing the `Trade.state`.   
<div class='code'>-----  Request  -----
PUT http://localhost:8081/rest/v1/trades/1
Headers:  {Authorization: 4816-5640-1027}{Content-Type: application/json}

{
  "name" : "Board games in Ottawa",
  "state" : "MATCHING_ITEMS",
  "tradeId" : 1
}

-----  Response  -----
Status:   HTTP/1.1 200 

{
  "name" : "Board games in Ottawa",
  "tradeId" : 1,
  "state" : "MATCHING_ITEMS",
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trades/1"
  } ]
}
</div>




[1]: rest/authenticate.md
[2]: rest/authentications.md
[3]: rest/trades.md
[4]: rest/users.md
[5]: https://www.w3.org/TR/2011/REC-ws-metadata-exchange-20111213/#terms
[6]: https://rafaelsantosbra.wordpress.com/2016/10/18/many-to-many-relationships-for-rest-api-with-a-relationship-attribute/ "REST API Many to Many relationship"
[7]: https://developer.atlassian.com/confdev/confluence-server-rest-api/expansions-in-the-rest-api "Atlassian Developers - Expansions in the REST API"
[8]: https://spring.io/understanding/HATEOAS "Spring HATEOAS"
[9]: https://developer.github.com/guides/traversing-with-pagination/ "GitHub Pagination"
[10]: https://www.w3.org/wiki/LinkHeader "LinkHeader Specification"
[11]: http://www.rfc-editor.org/rfc/rfc5988.txt "rfc5988"
[12]: rest/trade-memberships.md
[13]: rest/items.md</body>
</html>
