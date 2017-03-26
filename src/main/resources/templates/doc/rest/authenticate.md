Authenticate
============
This endpoint (endpoints do not necessarily operate on resources. See ["Web Services - Terminology](https://www.w3.org/TR/2011/REC-ws-metadata-exchange-20111213/#terms)) is going to redirect the client to an oAuth server and later redirected back to the consumer with an header called `Authorization`.

The `Authorization` header needs to be included to any secured REST resources.

${AUTHENTICATE_SNIPPET}
