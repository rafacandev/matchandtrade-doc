Trades
======
Trades are the central components of Match and Trade. The user who creates a trade becomes the _organizer_ and people can join the trade and became _members_. _Members_ submit their `TODO ADD LINK HERE Trade Items`. Later the _organizer_ closes the trade and the application generates the results of the trade.

When a new Trade is create, the `userId` associated to the `Authorization` header will be assigned as the trade _organizer_. 

#### Operations
* `GET rest/v1/trades/{tradeId}`
* `POST rest/v1/trades/{tradeId}`

#### Authorization
* Only authenticated clients.

#### Resource
| Field Name | Rules | Description |
| ---------- | ----- | ----------- |
name | read-write, unique, 200 max length | The name for this trade
tradeId | Read-only | Id associated with this resource

#### Examples
${TRADES_POST_SNIPPET}

${TRADES_GET_SNIPPET}
