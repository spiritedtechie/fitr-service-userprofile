Description
-----------

A simple microservice for user profile data and profile images using Dropwizard and MongoDB.

It utilises end-to-end streaming for images (client to database, and vice-versa) to prevent out of memory errors.

Auth
----

The API expects a JWT token to be provided in the header of each API request. This JWT token should be obtained
from login via the fitr-service-user service (a centralised auth microservice).

All JWT tokens are validated against this auth service.

Technologies
------------

- Dropwizard Core - JAXRS, Jersey
- Dropwizard Auth
- Mongojack
- MongoDB & GridFS

Setup
-----

Install MongoDB (easiest way is as a Docker container e.g. via Kitematic)

Configure local.yml with MongoDB host, port and database name

Running
-------

Run the service:

    gradle run