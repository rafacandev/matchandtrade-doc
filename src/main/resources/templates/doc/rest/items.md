Trade Memberships
=================
_Resource_ for Items. Items are included in a [trade membership][1] to indicate which items a [user][2] wants to exchange in a [trade][3].  

#### Operations
* `POST rest/v1/trade-memberships/`

#### Authorization
* Only authenticated clients.

#### Resource
| Field Name | Rules | Description |
| ---------- | ----- | ----------- |

##### Query Parameters
| Field Name | Description |
| ---------- | ----------- |

#### Rules
* Cannot POST items with the same name within the same [trade-membership][1].

#### Examples
${ITEMS_POST_SNIPPET}

[1]: trade-memberships.md
[2]: users.md
[3]: trades.md
