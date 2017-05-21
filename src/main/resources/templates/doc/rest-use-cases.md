Use Cases
=========
Typical use cases usage.

#### Create a Trade
Authenticate to the application.
${AUTHENTICATE_SNIPPET}

Verify the authentication details. Note that you need to pass the `Authorization` header to all secured endpoints. 
${AUTHENTICATIONS_SNIPPET}

Create a [Trade][3]
${TRADES_POST_SNIPPET}

Note that the `state` for the created [trade][3] is `SUBMITTING_ITEMS` meaning that [users][4] are allowed to subscribe and submit [items][13].

#### Became Member of a Trade
Authenticate with a second [user][4] which is going to become a _member_ of [trade][3] previously created.
${AUTHENTICATE_SNIPPET_SECOND}

Verify the authentication details for the second user. The `userId` is going to be used on [TradeMemberships][4].
${AUTHENTICATIONS_SNIPPET_SECOND}

Become member of the [trade][3].
${TRADES_MEMBERSHIP_POST_SNIPPET}


[1]: rest/authenticate.md
[2]: rest/authentications.md
[3]: rest/trades.md
[4]: rest/users.md
[5]: https://www.w3.org/TR/2011/REC-ws-metadata-exchange-20111213/#terms
[6]: https://rafaelsantosbra.wordpress.com/2016/10/18/many-to-many-relationships-for-rest-api-with-a-relationship-attribute/ "REST API Many to Many relationship"
[7]: https://developer.atlassian.com/confdev/confluence-server-rest-api/expansions-in-the-rest-api "Atlassian Developers - Expansions in the REST API"
[8]: https://spring.io/understanding/HATEOAS "Spring HATEOAS"
[9]: https://developer.github.com/guides/traversing-with-pagination/ "GitHub Pagination"
[10]: https://www.w3.org/wiki/LinkHeader "LinkHeader Specification"
[11]: http://www.rfc-editor.org/rfc/rfc5988.txt "rfc5988"
[12]: rest/trade-memberships.md
[13]: rest/items.md