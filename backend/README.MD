## How to run the backend

1. In your Maven side panel select Studyboard -> Lifecycle -> package and Run Maven Clean + Install
```bash
mvn clean install
```

2. Start a local docker server

3. In your terminal go to local and execute
```bash
docker-compose -f local/docker-compose-db.yml up
```
to start the database container or
```bash
docker-compose -f local/docker-compose.yml up
```
to start both, database and application, containers.
4. (Optional) Run StudyboardApplication if you have not deployed the application container in the step before.
Use `-Dspring.profiles.active=local` JVM system parameter

If the postgres container won't run with docker (Docker displays error: Database is uninitialized and superuser password is not specified) you can add the line

POSTGRES_HOST_AUTH_METHOD: "trust"

to the .env file in the local folder. After the first Run and initialization of the database you can remove the line from the .env file.
