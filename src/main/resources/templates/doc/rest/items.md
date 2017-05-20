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
name | read-write, unique, 3 min length, 150 max length, must be unique (case insensitive) within a given TradeMembership | Name of this item

##### Query Parameters
| Field Name | Description |
| ---------- | ----------- |
name | Resources matching `Item.name`
_pageSize | See [pagination][5]
_pageNumber | See [pagination][5]


#### Examples
${ITEMS_POST_SNIPPET}

${ITEMS_PUT_SNIPPET}

${ITEMS_GET_SNIPPET}

${ITEMS_SEARCH_SNIPPET}

${ITEMS_GET_ALL_SNIPPET}

[1]: trade-memberships.md
[2]: users.md
[3]: trades.md
[4]: authentications.md
[5]: ../rest-guide.md