RESTful Documentation
=====================
Match And Trade provides a powerful RESTful API which other applications may interact with.

Resources
---------
Here is the list of resources available thought the RESTful API.

* [/authenticate][1]
* [/authentications][2]


General Guidelines
------------------
Generally speaking, resources and payloads have consistent formats. Furthermore, [expanding resources][7] is discouraged and multiple asynchronous calls are favored in order to load sub-resources.
### HATEOAS
The importance of HATEOAS cannot be emphasized enough. This app uses the [Spring HATEOAS][8] approach to handle hypermedia.
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



[1]: rest/authenticate.md
[2]: rest/authentications.md
[6]: https://rafaelsantosbra.wordpress.com/2016/10/18/many-to-many-relationships-for-rest-api-with-a-relationship-attribute/ "REST API Many to Many relationship"
[7]: http://venkat.io/posts/expanding-your-rest-api/ "REST API Expand Resources"
[8]: https://spring.io/understanding/HATEOAS "Spring HATEOAS"
[9]: https://developer.github.com/guides/traversing-with-pagination/ "GitHub Pagination"
[10]: https://www.w3.org/wiki/LinkHeader "LinkHeader Specification"
[11]: http://www.rfc-editor.org/rfc/rfc5988.txt "rfc5988"