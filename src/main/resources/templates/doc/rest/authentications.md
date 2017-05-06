Authentications
===============
_Resource_ for authentications. It returns information about the current authenticated session, typically used to get the `userId` associated with the current session.

#### Operations
* `GET rest/v1/authentications`

#### Authorization
* Only authenticated clients.

#### Resource
| Field Name | Rules | Description |
| ---------- | ----- | ----------- |
userId | read-only | The [user][2] associated with this authentication, this value is assigned during the sign-up process

#### Example
${AUTHENTICATIONS_SNIPPET}

[2]: users.md