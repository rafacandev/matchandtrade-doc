Match and Trade Document
========================

This project creates detailed documentation for [Match And Trade Web][1]

The released version of this documentation is hosted at: [Match And Trade Documentation][2]

### Generating the documentation

Run [Match And Trade Web][1] with the configuration located at `docker/config/matchandtrade.properties`.
Then, run maven's default goal. Example:

```
cp ~/matchandtrade-doc/docker/config/matchandtrade.properties
cd ~/matchandtrade-web-api
mvn
cd ~/matchandtrade-doc
mvn
```

The application will perform a series of tests and generate the documentation.
Hence, all examples in the documentation are real and functional examples,
this approach promotes precision and also serves as a high level testing.

Finally, to publish the documentation to _GitHub Pages_ replace the content of the folder `docs`
with the content from `target/doc-maker` which will automatically published. Example:

```
rm -rf docs
mvn
cp -r target/rest-doc-maker docs
git add docs
git commit -m "Updated 'docs' folder with the current version"
git push
```

[1]: https://github.com/rafasantos/matchandtrade
[2]: https://rafasantos.github.io/matchandtrade-doc/
