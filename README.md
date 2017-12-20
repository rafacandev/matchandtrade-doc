Match and Trade Document Maker
==============================

This project creates detailed documentation for [Match And Trade Web API][1].

### Generating the documentation

```
mvn clean package
```

The application will start the embedded web server on the port 8081, perform a series or tests and generate the documentation. Hence, all examples in the documentation are real and functional examples, this approach promotes precision and also serves as high level testing. Open the file `target/doc-maker/index.html` to explore the documentation.

Finally, to publish the documentation to _GitHub Pages_ replace the content of the folder `docs` with the content from `target/doc-maker` which is automatically published [here][2]. Example:

```
rm -rf docs
cp -r target/rest-doc-maker
git add docs
git commit -m "Updated 'docs' folder with the content from 'target/rest-doc-maker'"
git push
```

### Extra features

Generate the documentation and keeps `matchandtrade-api` running in the background. This allows to interacts with the API on http://localhost:8081

```
mvn -Dmatchandtrade.doc.stop.webserver=false
```

[1]: https://github.com/rafasantos/matchandtrade
[2]: https://rafasantos.github.io/matchandtrade-doc/
