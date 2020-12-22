## How to run the backend

1. In your Maven side panel select Studyboard -> Lifecycle -> package and Run Maven Clean + Install
```bash
mvn clean install
```
2. Start a local docker server

##### To run BE in an IDE:
4.1. In your terminal execute
```bash
docker-compose -f local/docker-compose-db.yml up
```
4.2  Run StudyboardApplication. Use the`-Dspring.profiles.active=local` profile (in VM Options). This mode will not produce any transcription.

To test whole functionality in the IDE:
* Create [Google Credentials](https://cloud.google.com/docs/authentication/getting-started).  
* Add `GOOGLE_APPLICATION_CREDENTIALS=/<path>/<to>/<credentials>.json` variable to the run configuration.
* Install [FFMPEG](https://ffmpeg.org/download.html).
* (If using Windows host) Add program argument `-Dffmpeg.path=/<path>/<to>/<ffmpeg>.exe` 
* Use `-Dspring.profiles.active=prod` profile.
* Run the configuration.

##### To run BE in a docker container:

4.1. Create [Google Credentials](https://cloud.google.com/docs/authentication/getting-started)

4.2. Move the .json file to `./transcriber/src/main/resources`

4.3. Run the command below to start both, database and application, containers.

```bash
docker-compose -f local/docker-compose.yml up
```


If the postgres container won't run with docker (Docker displays error: Database is uninitialized and superuser password is not specified) you can add the line
`
POSTGRES_HOST_AUTH_METHOD: "trust"
`
to the .env file in the local folder. After the first Run and initialization of the database you can remove the line from the .env file.
