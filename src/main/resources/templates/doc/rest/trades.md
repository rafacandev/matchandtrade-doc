Trades
======
_Resource_ for trades. Trades are the the central components of the system. The [user][1] who creates a trade becomes the _organizer_ while other [users][1] can subscribe to the trade and become _members_.
_Members_ submit their [items][3] through a [trade membership][4]. Later the _organizer_ closes the trade and the system generates the results of the trade.

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
state	| read-write, enumeration (see values below) | The state of this trade, states drive how to interact with the trade.
name	| read-write, mandatory, unique, 3 min length, 150 max length | The name of this trade
tradeId	| read-only | Id associated with this resource

##### State
| Name | Description |
| ---- | ----------- |
SUBMITTING_ITEMS | Default state when a trade is created. Indicates that users can join the trade and submit [items][3].
MATCHING_ITEMS | [Users][1] can match [items][3] in the trade. Other [users][1] cannot join the trade nor submit [items][3].
GENERATING_TRADES | The system is generating the trade results.
CLOSED | The trade is in ready-only mode

##### Query Parameters
| Field Name | Description |
| ---------- | ----------- |
name | Resources matching `Trade.name`
_pageSize | See [pagination][2]
_pageNumber | See [pagination][2]

#### Rules
* When a new trade is created, the [user][1] associated to the `Authorization` header will be the trade _organizer_.
* Only the trade _organizer_ can DELETE a trade.
* Only the trade _organizer_ can PUT a trade.

#### Examples
${TRADES_POST_SNIPPET}

${TRADES_PUT_SNIPPET}

${TRADES_GET_SNIPPET}

${TRADES_SEARCH_SNIPPET}

${TRADES_GET_ALL_SNIPPET}

${TRADES_DELETE_SNIPPET}

[1]: users.md
[2]: ../rest-guide.md
[3]: items.md
[4]: trade-memberships.md