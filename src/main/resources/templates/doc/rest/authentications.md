Authentications
===============
Information about the current authenticated session. Typically this resource is used to get the `userId` associated with the current session.


#### Methods
* `GET rest/v1/authentications`

#### Authorization
* Only authenticated clients.

#### Resource
| Field Name | Rules | Description |
| ---------- | ----- | ----------- |
authenticationId | Read-only | Id associated with this authentication, this value is assigned during the [authenticate][1] process
userId | Read-only | The [user][2] associated with this authentication, this value is assigned during the sign-up process

#### Example

${AUTHENTICATIONS_SNIPPET}


[1]: authenticate.md
[2]: users.md