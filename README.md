Description
-----------

A simple microservice for user profile data using Dropwizard and MongoDB.

It utilises streams both inbound and outbound to prevent out of memory errors.

It auths against fitr-service-user (a centralised auth microservice), expecting a JWT token to be provided in the header of each API request.

Technologies
------------
- Dropwizard Core - JAXRS, Jersey
- Dropwizard Auth
- Mongojack
- MongoDB

Setup
-----

Install MongoDB (easiest way is as a Docker container e.g. via Kitematic)

Configure local.yml with MongoDB host, port and database name

Running
-------

Run the service:

    gradle run