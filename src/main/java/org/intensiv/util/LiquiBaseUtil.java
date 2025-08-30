package org.intensiv.util;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;

public class LiquiBaseUtil {
    public static void runMigrations() {
        String url = "jdbc:postgresql://localhost:5432/testdb";
        String user = "postgres";
        String password = "1";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            Database database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(new JdbcConnection(conn));

            Liquibase liquibase = new Liquibase(
                    "db/changelog/db.changelog-master.xml",
                    new ClassLoaderResourceAccessor(),
                    database
            );

            liquibase.update("");
            liquibase.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
