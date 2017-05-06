RESTful Documentation
=====================
Match And Trade provides a powerful RESTful API which other applications may interact with.

Resources
---------
Here is the list of resources available thought the RESTful API.

* [authenticate][1]
* [authentications][2]
* [trades][3]
* [trade-memberships][12]
* [users][4]

General Guidelines
------------------
Generally speaking, _resources_ have consistent behavior which _endpoints_ may vary in format and behavior.

### Endpoints vs Resources
_Resource_ URLs are a specialized _endpoints_ which provides standard operations over a resource. In this application, all _resources_ support HATEOAS, pagination and follow standard query parameters.

On the other hand, _endpoint_ URLs may not follow all constraints of resource URLs. Some may also perform actions (e.g.: [authenticate][1]). See [Web Services - Terminology][5] for more details.

### PUT vs PATCH
`PUT` requests are favored over `PATH` requests. While `PATH` may offer smaller payloads, it also introduces development complexity for little benefit.

Additionally, when doing a `PUT` or `PATCH` it is recommended to do not include the `id` for the given resource in the payload. For example, when doing a `PUT /trades/{tradeId}` do not include `tradeId` in the payload, it will be simply ignored.

```
-----  Request  -----
PUT http://localhost:8081/rest/v1/trades/1
Headers:  {Authorization: 7075-1255-9178}{Content-Type: application/json}

{
  // Do not enter tradeId in this payload, it will be ignored.
  "name" : "Name to update"
}

-----  Response  -----
Status:   HTTP/1.1 200 

{
  "name" : "Name to update",
  "tradeId" : 1,
  "_links" : [ {
    "rel" : "self",
    "href" : "http://localhost:8081/rest/v1/trades/1"
  } ]
}
```


### HATEOAS
The importance of HATEOAS cannot be emphasized enough. This application uses the [Spring HATEOAS][8] approach to handle hypermedia.

### Expanding
Expanding resources (see [Atlassian API][7] for an example) is discouraged while multiple asynchronous calls are favored.

### Many To Many Relationships
Relationships are treated as resources similarly to what is described on this [post][6].

### Pagination
REST clients should rely on the pagination information which is included in responses with multiple results. Our pagination follows the [LinkHeader][10] specification along with [RFC5988][11]. It works similarly to [GitHub's pagination][9].

When performing a GET to resources that returns an array you can pass the query parameters `_pageSize` to indicate the number of records returned in a page and `_pageNumber` to indicate which page number you want to return. Note that page numbers start at number 1.

Typical Workflow
----------------
Authenticate to the application.
${AUTHENTICATE_SNIPPET}

Verify the authentication details. Note that you need to pass the `Authorization` header obtained on the previous response. 
${AUTHENTICATIONS_SNIPPET}

Create a [Trade][3]
${TRADES_POST_SNIPPET}

Authenticate as a second [user][4] which is going to become a [trade][3] _member_;
${AUTHENTICATE_SNIPPET_SECOND}

Verify the authentication details for the second user. The `userId` is going to be used on [TradeMemberships][4].
${AUTHENTICATIONS_SNIPPET_SECOND}

The second user becomes member of the [trade][3].
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