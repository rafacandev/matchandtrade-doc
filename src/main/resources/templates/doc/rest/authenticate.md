Authenticate
============
_Endpoint_ for authentication. It redirects the client to an oAuth server and returns back to the client with a header called `Authorization`.

The `Authorization` header acts as a session token and needs to be included to any secured URL.

#### Operations
* `GET authenticate`
* `GET authenticate/sign-out`

#### Authorization
* Public

#### Examples
${AUTHENTICATE_SNIPPET}

${SIGN_OFF_SNIPPET}
