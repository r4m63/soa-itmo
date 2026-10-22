# SOA

```sh
payara7/bin/asadmin create-domain --nopassword movie-service
payara7/bin/asadmin create-domain --portbase 10000 --nopassword oscar-service

payara7/bin/asadmin start-domain movie-service
payara7/bin/asadmin start-domain oscar-service
payara7/bin/asadmin list-domains

payara7/bin/asadmin add-library ~/Downloads/postgresql-42.7.14.jar
payara7/bin/asadmin restart-domain movie-service
payara7/bin/asadmin list-libraries

payara7/bin/asadmin create-jdbc-connection-pool --datasourceclassname org.postgresql.ds.PGSimpleDataSource --restype javax.sql.DataSource --property user=movies:password=movies:serverName=localhost:portNumber=5432:databaseName=movies movies-pool
payara7/bin/asadmin create-jdbc-resource --connectionpoolid movies-pool jdbc/movies

payara7/bin/asadmin ping-connection-pool movies-pool

```