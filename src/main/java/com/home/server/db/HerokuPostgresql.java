package com.home.server.db;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.logging.BotLogger;

import java.sql.*;

@Slf4j
public class HerokuPostgresql {

    // Database credentials
    private static final String DB_URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASS = System.getenv("DB_PASS");
    private static final Integer QUERY_TIMEOUT = 30; // seconds

    private static final String LOGTAG = "CONNECTIONDB";

    public void initPostgresDb() {

        log.debug("Testing connection to PostgreSQL JDBC");
        log.debug(DB_URL);

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            BotLogger.error(LOGTAG, "PostgreSQL JDBC Driver is not found. Include it in your library path ");
            //log.debug("PostgreSQL JDBC Driver is not found. Include it in your library path ");
            e.printStackTrace();
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
            statement.executeUpdate("create table if not exists COC.PLR(plr_id serial primary key, " +
                    "username varchar (50) not null, " +
                    "tag varchar (50) unique not null, " +
                    "trophies integer, " +
                    "vs_trophies integer, " +
                    "th integer)"
            );

            log.debug("Create table CONFIG...");
            statement.executeUpdate("CREATE TABLE IF NOT EXISTS COC.CONFIG " +
                    "(conf_id serial PRIMARY KEY, " +
                    "conf_name VARCHAR (50) NOT NULL, " +
                    "description VARCHAR (50), " +
                    "value_string VARCHAR (50), " +
                    "value_int INTEGER)"
            );

            // https://www.postgresqltutorial.com/postgresql-insert/
            log.debug("Insert demo player...");
            statement.executeUpdate("insert into COC.PLR" +
                    "(username, tag, trophies, vs_trophies, th) " +
                    "values('DEMO_NAME', 'DEMO_TAG', 2643, 4283, 13)");

        } catch (SQLException e) {
            log.debug("Connection Failed");
            BotLogger.error(LOGTAG, "PostgreSQL JDBC Driver is not found. Include it in your library path ");
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

    public String getToken() {
        String coc_token = null;
        Connection connection = null;

        try {
            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);

            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery("select * from COC.CONFIG where conf_id = 1");
            while (rs.next()) {
                log.debug("Read data from query...");
                coc_token = rs.getString("conf_value");
                log.debug(
                        "\n\tCONF_ID = " + rs.getInt("conf_id") +
                                "\n\tCONF_NAME = " + rs.getString("conf_name") +
                                "\n\tCONF_VALUE = " + rs.getString("conf_value")
                );
            }

        } catch (SQLException e) {
            BotLogger.error(LOGTAG, e.getMessage());
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
        return coc_token;
        //log.debug("COC token is : " + coc_token);
    }
}
