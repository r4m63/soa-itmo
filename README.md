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

payara7/bin/asadmin --port 4848 set configs.config.server-config.network-config.network-listeners.network-listener.http-listener-1.enabled=false
payara7/bin/asadmin --port 10048 set configs.config.server-config.network-config.network-listeners.network-listener.http-listener-1.enabled=false

keytool -delete -alias s1as -keystore payara7/glassfish/domains/oscar-service/config/keystore.p12 -storepass changeit
keytool -genkeypair -alias s1as -keyalg RSA -keysize 2048 -validity 3650 -dname "CN=localhost, OU=Payara, O=Payara Foundation, L=Great Malvern, ST=Worcestershire, C=UK" -ext san=dns:localhost,ip:127.0.0.1 -keystore payara7/glassfish/domains/oscar-service/config/keystore.p12 -storepass changeit

keytool -exportcert -alias s1as -keystore payara7/glassfish/domains/movie-service/config/keystore.p12 -storepass changeit -file movie-service.cer
keytool -importcert -noprompt -alias movie-service -file movie-service.cer -keystore payara7/glassfish/domains/oscar-service/config/cacerts.p12 -storepass changeit

payara7/bin/asadmin restart-domain movie-service
payara7/bin/asadmin restart-domain oscar-service

```