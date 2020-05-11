package com.home.server.db;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class HerokuPostgresql {

    // Database credentials
    private static final String DB_URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASS = System.getenv("DB_PASS");
    private static final Integer QUERY_TIMEOUT = 30; // seconds


    //    public static void main(String[] argv) {
    public void initPostgreDb() {

        log.debug("Testing connection to PostgreSQL JDBC");
        log.debug(DB_URL);

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            log.debug("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
            return;
        }

        log.debug("PostgreSQL JDBC Driver successfully connected");
        Connection connection = null;

        try {
            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);

            Statement statement = connection.createStatement();

            log.debug("Set query timeout = " + QUERY_TIMEOUT);
            statement.setQueryTimeout(QUERY_TIMEOUT);  // set timeout to 30 sec.

            // https://www.postgresqltutorial.com/postgresql-create-schema/
            log.debug("Create scheme COC...");
            statement.executeUpdate("CREATE SCHEMA IF NOT EXISTS coc");

            // https://www.postgresqltutorial.com/postgresql-create-table/
            log.debug("Create table PLR...");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS COC.PLR" +
                    "(plr_id serial PRIMARY KEY," +
                    "username VARCHAR (50) NOT NULL," +
                    "tag VARCHAR (50) UNIQUE NOT NULL)"
            );

            log.debug("Create table CONFIG...");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS COC.CONFIG" +
                    "(conf_id serial PRIMARY KEY," +
                    "conf_name VARCHAR (50) NOT NULL," +
                    "description VARCHAR (50))," +
                    "value_string VARCHAR (50))," +
                    "value_int INTEGER)"
            );

            // https://www.postgresqltutorial.com/postgresql-insert/
            log.debug("Insert demo player...");
            statement.executeUpdate("INSERT INTO COC.PLR VALUES(1, 'DEMO_NAME', 'DEMO_TAG')");

        } catch (SQLException e) {
            log.debug("Connection Failed");
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                    log.debug("connection closed ...");
                }

            } catch (SQLException e) {
                // connection close failed.
                log.error(e.getMessage());
            }
        }
    }
}
