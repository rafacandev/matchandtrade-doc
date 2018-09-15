Docker Image Dependencies for Match And Trade Doc
=================================================

Description
-----------
This docker-compose which starts a `matchandtrade-api` server configured with `com.matchandtrade.authentication.AuthenticationOAuthNewUserMock`,
so every Authentication request generates a new user which greatly facilitates testing as it abstracts oAuth complexity.

To start the docker-compose enter:
```
sudo docker-compose up
```

See `matchandtrade-api` for instructions about generating a docker image `matchandtrade-api:0.0-SNAPSHOT`, so you can always use the latest version.
