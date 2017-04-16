Trade Memberships
=================
Members of a `trade`. Members are existing `user`s who participate in a `trade`.  

#### Operations
* `POST rest/v1/trade-memberships/`
* `GET rest/v1/trade-memberships/{tradeMembershipId}`
* `GET rest/v1/trade-memberships?{queryParameters}`
* `GET rest/v1/trade-memberships/`
* `DELETE rest/v1/trade-memberships/{tradeMembershipId}`

##### Query Parameters
| Field Name | Description |
| ---------- | ----------- |
userId | Resources matching `TradeMembership.userId`
tradeId | Resources matching `TradeMembership.tradeId`
_pageSize | See pagination details
_pageNumber | See pagination details


#### Authorization
* Only authenticated clients.

#### Resource
| Field Name | Rules | Description |
| ---------- | ----- | ----------- |
tradeMembershipId | read-only | Id associated with this resource
userId | read-write | Must belong to an existing `user`
tradeId | read-write | Must belong to an existing `trade`

#### Rules
* Cannot delete the owner of a `trade`

#### Examples
${TRADES_MEMBERSHIP_POST_SNIPPET}

${TRADES_MEMBERSHIP_GET_SNIPPET}

${TRADES_MEMBERSHIP_SEARCH_SNIPPET}

${TRADES_MEMBERSHIP_GET_ALL_SNIPPET}