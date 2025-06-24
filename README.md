# Example on a live database table viewer

A read only UI that shows the contents of the Postgres database table `item`.
The idea is to use a database tool such as `psql` to modify the database table and see the changes in the browser.

## Running the example using test containers (the easy way)

Start the application using `mvn spring-boot:test-run` or launch the `TestApplication` class in your IDE.

Look at the output for a row in the output like 
```
Container is started (JDBC URL: jdbc:postgresql://localhost:64548/test?loggerLevel=OFF)
```

Grab the port number from there and run
```
psql -h localhost -p <port> --user test
```
enter `test` as the password and you can modify the database table e.g. as
```
insert into item VALUES (5,'Zucchini',12);
```

Any changes made in the database should immediately show up in the browser.


## Running the example with a local Postgres

If you have a local postgres database up and running you can create the needed database and table using
```
createuser livedb -P
createdb livedb -O livedb
```

Then you can run the application by launching the `Application` class in your IDE or by running
```
mvn spring-boot:run
```

Connect to the local Postgres database in any way you like, e.g. using `psql` as above (but with `livedb` as username and password and the standard port)
