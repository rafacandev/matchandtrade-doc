Authenticate
============
Information about the current authenticated session. In many cases, this resource is the used to get the `userId` associated with the current session.

This authentication request is going to redirect the client to a oAuth server and later redirected back with an header called `Authorization` which needs to be passed to secured REST resources.

${AUTHENTICATE_POSITIVE_SNIPPET}
