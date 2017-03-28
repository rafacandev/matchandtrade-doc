Users
======
Resource to manage the users in the system. Typically, users are created in the application on the first time they authenticate in the system. The oAuth process will create the user, see [Authenticate][1] endpoint for more details.

#### Operations
* `GET rest/v1/users/{userId}`
* `PUT rest/v1/users/{userId}`

#### Authorization
* Only authenticated clients.
* Clients can only get the resource associated with their own authenticated session. In other words, firstly the client needs to get the `userId` from [authentications][2] and pass it in the URL. 

#### Resource
| Field Name | Rules | Description |
| ---------- | ----- | ----------- |
email | Read-only | The user's email address, this value is assigned by the Authentication Authority during the sign-up process
name | Read-write, 200max | The user's name this value is firstly assigned by the Authentication Authority during the sign-up process
userId | Read-only | Id associated with this resource

#### Examples
${USERS_GET_SNIPPET}

${USERS_PUT_SNIPPET}

[1]: authenticate.md
[2]: authentications.md