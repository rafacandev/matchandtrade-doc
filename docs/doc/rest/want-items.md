<!DOCTYPE html>
<html>
	<head>
		<meta charset='UTF-8'>
		<link rel='stylesheet' href='css/combined-style.css'>
		<title>Match and Trade - Documentation</title>
	</head>
<body>
WantItem
========
_Resource_ for Want Items. A Want Item is an [item][2] that could be exchanged for another [item][2]. Example: the URL `GET /rest/v1/trade-memberships/1/items/2/want-items` could be translated as: _get me all the [items][2] that could be exchanged for the `Item.itemId: 2`_.  

#### Operations
* `POST /rest/v1/trade-memberships/{tradeMembershipId}/items/{itemId}/want-items/`
* `GET /rest/v1/trade-memberships/{tradeMembershipId}/items/{itemId}/want-items/{wantItemId}`
* `GET /rest/v1/trade-memberships/{tradeMembershipId}/items/{itemId}/want-items/`
* `DELETE /rest/v1/trade-memberships/{tradeMembershipId}/items/{itemId}/want-items/{wantItemId}`

#### Authorization
* Only authenticated clients.

#### Resource
| Field Name | Rules | Description |
| ---------- | ----- | ----------- |
itemId | ready-write, mandatory | Must belong to an existing [item][2] within the same [trade][3]
priority | ready-write, mandatory, integer between 1 and 1000 | Priority for this [WantItem][1]. The lower the number the more wanted the [item][2] is.
wantItemId | read-only | Id associated with this resource

#### Rules
* `WantItem.itemId` must belong to an existing [item][2] within the current [trade][3].

#### Examples
<div class='code'>-----  Request  -----
POST http://localhost:8081/rest/v1/trade-memberships/11/items/7/want-items
Headers:  {Authorization: 7449-8701-9362}{Content-Type: application/json}

{
  "itemId" : 8,
  "priority" : 1,
  "wantItemId" : null
}

-----  Response  -----
Status:   HTTP/1.1 201 

{
  "wantItemId" : 1,
  "itemId" : 8,
  "priority" : 1,
  "_links" : [ {
    "rel" : "item",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/11/items/8"
  }, {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/11/items/7/want-items/8"
  } ]
}
</div>

<div class='code'>-----  Request  -----
GET http://localhost:8081/rest/v1/trade-memberships/11/items/7/want-items
Headers:  {Authorization: 7449-8701-9362}{Content-Type: application/json}

-----  Response  -----
Status:   HTTP/1.1 200 
Headers:  {X-Pagination-Total-Count: 1}

[ {
  "wantItemId" : 1,
  "itemId" : 8,
  "priority" : 1,
  "_links" : [ {
    "rel" : "item",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/11/items/8"
  }, {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trade-memberships/11/items/7/want-items/8"
  } ]
} ]
</div>


[1]: #WantItem
[2]: items.md
[3]: trades.md
</body>
</html>
