Trades
======
_Resource_ for trades. Trades are one of the the central components of Match and Trade. The [user][1] who creates a trade becomes the _organizer_ while other [users][1] can join the trade and become _members_.
_Members_ submit their `TODO Trade Items`. Later the _organizer_ closes the trade and the application generates the results of the trade.

#### Operations
* `POST rest/v1/trades/`
* `PUT rest/v1/trades/{tradeId}`
* `GET rest/v1/trades/{tradeId}`
* `GET rest/v1/trades?{queryParameters}`
* `GET rest/v1/trades/`
* `DELETE rest/v1/trades/{tradeId}`

#### Authorization
* Only authenticated clients.

#### Resource
| Field Name | Rules | Description |
| ---------- | ----- | ----------- |
name | read-write, unique, 3 min length, 150 max length | The name of this trade
tradeId | read-only | Id associated with this resource

##### Query Parameters
| Field Name | Description |
| ---------- | ----------- |
name | Resources matching `Trade.name`
_pageSize | See [pagination][2]
_pageNumber | See [pagination][2]

#### Rules
* When a new trade is created, the [user][1] associated to the `Authorization` header will be the trade _organizer_.
* Only the trade _organizer_ can delete a trade.

#### Examples
${TRADES_POST_SNIPPET}

${TRADES_PUT_SNIPPET}

${TRADES_GET_SNIPPET}

${TRADES_SEARCH_SNIPPET}

${TRADES_GET_ALL_SNIPPET}

${TRADES_DELETE_SNIPPET}

[1]: users.md
[2]: ../rest-guide.md