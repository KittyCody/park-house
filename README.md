![tests workflow](https://github.com/KittyCody/park-house/actions/workflows/tests.yml/badge.svg)

# Park House
Parking house management software developed using Spring Boot.

## Local setup
The setup it containerized for easier local development.

```shell
docker-compose up
```

## Keycloak Realm

### Access tokens
To obtain an access token you can make a request to the authentication server. Development secrets are kept in plain text.
```shell
curl --request POST \
  --url http://localhost:9080/realms/park-house/protocol/openid-connect/token \
  --header 'Content-Type: application/x-www-form-urlencoded' \
  --data grant_type=password \
  --data client_id=park-house-client \
  --data client_secret=r3sGs9hPZctXTl3g8y6d9HHhcFCzxmR6 \
  --data username=gate_01 \
  --data 'password=gate01pass!'
```

There are three pre-existing users created in the realm.

- gate_01 : gate01pass!
- gate_02 : gate02pass!
- admin : adminpass!

### Editing the realm import
If the realm imported at startup needs to be modified, either edit the file manually,
or export an edited version of the realm using Docker. The import file is in `./keycloak/park-house-realm.json`.

#### Exporting a realm with Docker
In the Docker Desktop go to the Exec tab of the Keycloak container while it is running
and enter the following commands:
```shell
cd /opt/keycloak/bin/
sh ./kc.sh export --dir /tmp/keycloak/ --users realm_file
```

Take the exported file from the `/tmp/keycloak` directory replace the contents of the current import file with the new ones.

Make sure the `authorizationSettings` are removed after export.

## Database

```shell
mvn flyway:migrate
```
