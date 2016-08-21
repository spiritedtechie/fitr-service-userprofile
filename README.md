Description
-----------

A simple microservice for user profile data and profile images using Dropwizard and MongoDB.

It utilises end-to-end streaming for images (client to database, and vice-versa) to prevent out of memory errors.

Auth
----

The API expects a JWT token to be provided in the header (named 'authToken') of each API request.

This JWT token should be obtained from login via the fitr-service-auth service (a centralised auth microservice).

All JWT tokens provided are validated using the auth microservice. The JWT token holds the role of the user, and each resource is protected with role-based authorization.

Technologies
------------

- Dropwizard Core - JAXRS, Jersey
- Dropwizard Auth
- Java Security
- Mongojack
- MongoDB & GridFS

Dependencies
------------

- MongoDB
- Auth Microservice

Running in Development Mode
---------------------------

1) Install MongoDB (easiest way is as a Docker container e.g. via Kitematic)

2) Configure config-local.yml

    - MongoDB host, port and database name
    - Auth service url

3) Run the service:

    ./gradlew run

Build Distribution
------------------

There are two options here:

1) Create a zip distribution

    ./gradlew build
    cd build/distributions
    unzip fitr-service-userprofile-1.0-SNAPSHOT.zip
    cd fitr-service-userprofile-1.0-SNAPSHOT
    ./bin/fitr-service-userprofile server <path_to_local_config>

2) Create an uber jar

    ./gradlew oneJar
    cd build/libs
    java -jar fitr-service-userprofile-1.0-SNAPSHOT-standalone.jar server <path_to_local_config>

Docker Installation
-------------------

Docker Compose is used to create and/or link the following Docker containers:

1. MongoDB instance
3. The User Profile microservice
2. The Auth microservice (external link)

To get this running:

1) Install Docker Toolbox and start Kitematic. This will start a Docker VM in VirtualBox.

2) Configure Docker client to point to correct Docker machine

    eval $(docker-machine env default)

3) Ensure Docker container running for the Auth microservice (see here)

4) Change to the project root directory, then:

    ./gradlew clean oneJar
    docker-compose build
    docker-compose up -d

5) For debugging, use one or more of the following:

    docker ps
    docker logs <container_id>
    docker exec -it <container_id> bash

6) Fetch the Docker VM IP to use when testing the APIs:

    docker-machine ip default

Testing
-------

PUT (Update) Profile

    curl -k -H "Content-Type: application/json" -H "authToken: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJib2IxQGdtYWlsLmNvbSIsInJvbGUiOiJOT1JNQUwiLCJpZCI6IjU3YjljMmUyYzllNzdjMDAwMWU2NTIzOCJ9.BVjRZ7FFV40cmPeJl18_mbfvPbGfjMoBOoPKerE83GdvIle-h5RcUkGtOeWSJaXgpt5HjO5EmIu3heqwgDiAJQ" -X PUT -d '{"favouriteActivity":"walking","goals":[{"type":"distance","metric":"2000"}]}' https://<host>:<port>/user/profile

GET Profile

    curl -k -H "Content-Type: application/json" -H "authToken: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJib2IxQGdtYWlsLmNvbSIsInJvbGUiOiJOT1JNQUwiLCJpZCI6IjU3YjljMmUyYzllNzdjMDAwMWU2NTIzOCJ9.BVjRZ7FFV40cmPeJl18_mbfvPbGfjMoBOoPKerE83GdvIle-h5RcUkGtOeWSJaXgpt5HjO5EmIu3heqwgDiAJQ" -X GET https://<host>:<port>/user/profile

PUT (Update) Profile Image

    curl -k -F "profileimage=@/tmp/testImage.png" -H "authToken: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJib2IxQGdtYWlsLmNvbSIsInJvbGUiOiJOT1JNQUwiLCJpZCI6IjU3YjljMmUyYzllNzdjMDAwMWU2NTIzOCJ9.BVjRZ7FFV40cmPeJl18_mbfvPbGfjMoBOoPKerE83GdvIle-h5RcUkGtOeWSJaXgpt5HjO5EmIu3heqwgDiAJQ" -X PUT https://<host>:<port>/user/profile/image

GET Profile Image

    curl -k -H "authToken: eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJib2IxQGdtYWlsLmNvbSIsInJvbGUiOiJOT1JNQUwiLCJpZCI6IjU3YjljMmUyYzllNzdjMDAwMWU2NTIzOCJ9.BVjRZ7FFV40cmPeJl18_mbfvPbGfjMoBOoPKerE83GdvIle-h5RcUkGtOeWSJaXgpt5HjO5EmIu3heqwgDiAJQ" -X GET https://<host>:<port>/user/profile/image >> test.png

PUT Profile (with invalid JWT token)

    curl -k -H "Content-Type: application/json" -H "authToken: yJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJib2IxQGdtYWlsLmNvbSIsInJvbGUiOiJOT1JNQUwiLCJpZCI6IjU3YjljMmUyYzllNzdjMDAwMWU2NTIzOCJ9.BVjRZ7FFV40cmPeJl18_mbfvPbGfjMoBOoPKerE83GdvIle-h5RcUkGtOeWSJaXgpt5HjO5EmIu3heqwgDiAJQ" -X PUT -d '{"favouriteActivity":"walking","goals":[{"type":"distance","metric":"2000"}]}' https://<host>:<port>/user/profile
