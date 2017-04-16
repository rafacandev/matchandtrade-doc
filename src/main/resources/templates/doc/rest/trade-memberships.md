Trade Memberships
=================
Members of a `Trade`. Members are existing `User`s who participate in a `Trade`.  

#### Operations
* `POST rest/v1/trade-memberships/`
* `GET rest/v1/trade-memberships/{tradeMembershipId}`
* `DELETE rest/v1/trade-memberships/{tradeMembershipId}`

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
