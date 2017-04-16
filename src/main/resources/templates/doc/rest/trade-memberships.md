Trade Memberships
=================
Members of a `Trade`. Members are existing `User`s who participate in a `Trade`.  

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
userId | read-write | Must belong to an existing `User`
tradeId | read-write | Must belong to an existing `Trade`

#### Rules
* Cannot delete the owner of a `Trade`

#### Examples
${TRADES_MEMBERSHIP_POST_SNIPPET}

${TRADES_MEMBERSHIP_GET_SNIPPET}

${TRADES_MEMBERSHIP_SEARCH_SNIPPET}

${TRADES_MEMBERSHIP_GET_ALL_SNIPPET}