Users
======
Resource to manage the users in the system. Typically, users are created in the application on the first time they authenticate in the system. See [Authenticate][1] for more details.

#### Operations
* `PUT rest/v1/users/{userId}`
* `GET rest/v1/users/{userId}`

#### Authorization
* Only authenticated clients.
* Clients can only get the resource associated with their own authenticated session. In other words, firstly the client needs to get the `userId` from [authentications][2] and pass it in the URL. 

#### Resource
| Field Name | Rules | Description |
| ---------- | ----- | ----------- |
email | read-only | The user's email address, this value is assigned by the Authentication Authority during the sign-up process
name | read-write, 150 max length | The user's name this value is firstly assigned by the Authentication Authority during the sign-up process
userId | read-only | Id associated with this resource

#### Examples
${USERS_PUT_SNIPPET}

${USERS_GET_SNIPPET}

[1]: authenticate.md
[2]: authentications.md