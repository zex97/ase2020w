## How to run the backend

1. In your Maven side panel select Studyboard -> Lifecycle -> package and Run Maven Build

2. Run a local docker server

3. In your terminal go to local and execute "docker-compose up -d"

4. Run StudyboardApplication

If the postgres container won't run with docker (Docker displays error: Database is uninitialized and superuser password is not specified) you can add the line

POSTGRES_HOST_AUTH_METHOD: "trust"

to the .env file in the local folder. After the first Run and initialization of the database you can remove the line from the .env file.
