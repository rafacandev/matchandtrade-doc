Authentications
===============
This _endpoint_ returns information about the current authenticated session, typically used to get the `userId` associated with the current session.


#### Methods
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