Authenticate
============
This endpoint (endpoints do not necessarily operate any resource. See [Web Services - Terminology](https://www.w3.org/TR/2011/REC-ws-metadata-exchange-20111213/#terms)). Instead it is going to redirect the client to an oAuth server and later return back to the client with an header called `Authorization`.

The `Authorization` header acts as a session token and needs to be included to any secured REST resource.

#### Operations
* `GET authenticate`
* `GET authenticate/sign-out`

#### Authorization
* Public

#### Resource
| Field Name | Rules | Description |
| ---------- | ----- | ----------- |
userId | Read-only | The [user][1] associated with this authentication, this value is assigned during the sign-up process

#### Examples
${AUTHENTICATE_SNIPPET}

${SIGN_OFF_SNIPPET}

[1]: users.md