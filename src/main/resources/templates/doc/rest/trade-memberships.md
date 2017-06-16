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
userId | read-write | Must belong to an existing `user`
tradeId | read-write | Must belong to an existing `trade`
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
${TRADES_MEMBERSHIP_POST_SNIPPET}

${TRADES_MEMBERSHIP_GET_SNIPPET}

${TRADES_MEMBERSHIP_SEARCH_SNIPPET}

${TRADES_MEMBERSHIP_GET_ALL_SNIPPET}

${TRADES_MEMBERSHIP_DELETE_SNIPPET}

[1]: users.md
[2]: trades.md
[3]: ../rest-guide.md