# Example on a live database table viewer

This projects assumes you have a Postgres  up and running on
`localhost` with a database `livedb` and a user `livedb/livedb` with full access to it.

If you have a local postgres database up and running you can create the above using
```
createuser livedb -P
createdb livedb -O livedb
```

## Running the example

Start the application using `mvn`

The UI is read only so use a database tool such as `psql` to modify the database table and see the changes.