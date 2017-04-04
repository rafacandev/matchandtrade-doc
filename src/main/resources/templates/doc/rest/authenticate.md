Authenticate
============
This _endpoint_ redirects the client to an oAuth server and returns back to the client with a header called `Authorization`.

The `Authorization` header acts as a session token and needs to be included to any secured URL.

#### Operations
* `GET authenticate`
* `GET authenticate/sign-out`

#### Authorization
* Public

#### Resource
| Field Name | Rules | Description |
| ---------- | ----- | ----------- |
userId | read-only | The [user][1] associated with this authentication, this value is assigned during the sign-up process

#### Examples
${AUTHENTICATE_SNIPPET}

${SIGN_OFF_SNIPPET}

[1]: users.md