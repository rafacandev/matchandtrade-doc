Match and Trade Document Maker
==============================

This is project to create detailed documentation for [Match And Trade][1].

The documentation is intended to be generated in a separate process and subsequently copied over to [Match And Trade][1].

#### Generating the documentation
```
mvn clean package
```
Next, open the file `target/doc-maker/README.md` to explore the documentation. An HTML version `target/doc-maker/README.md.html` is also provided if you want to see the documentation in your web browser.

The application will start the embedded web server on the port 8081 with a in-memory this web sever is going to be used when generating RESTful examples. Hence, all examples in the documentation are real case and functional examples, this approach promotes precision and also serves as high level testing (the documentation may fail and it will report if bugs are found).
   
[1]: https://github.com/rafasantos/matchandtrade