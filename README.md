Match and Trade Document Maker
==============================

This project creates detailed documentation for [Match And Trade][1].

The documentation is intended to be generated as a separated process and subsequently copied over to `/docs` which is then published as _GitHub Pages_ available [here][2].

#### Generating the documentation

```
mvn clean package
```

The application will start the embedded web server on the port 8081, perform a series or tests and generate the documentation.
Hence, all examples in the documentation are real case and functional examples, this approach promotes precision and also serves as high level testing.
Next, open the file `target/doc-maker/index.html` to explore the documentation.

Finally, to publish the documentation to _GitHub Pages_ the most recent documentation needs to be copied to `/docs`. Example:

```
rm -rf docs/
cp -r target/doc-maker docs
git add docs/
git commit -am "Updated /docs as generated at `target/doc-maker`"
git push
```
   
[1]: https://github.com/rafasantos/matchandtrade
[2]: https://rafasantos.github.io/matchandtrade-doc/