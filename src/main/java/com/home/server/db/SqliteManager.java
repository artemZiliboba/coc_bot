package com.home.server.db;

import lombok.extern.slf4j.Slf4j;

import java.sql.*;

@Slf4j
public class SqliteManager {
    private static final String PATH_TO_DB = "jdbc:sqlite:E:/[SOFT]/SQLite/db/coc.s3db";

    // Sample method
    public static void main(String[] args) {
        Connection connection = null;
        try {
            // create a database connection
            connection = DriverManager.getConnection(PATH_TO_DB);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate("drop table if exists person");
            statement.executeUpdate("create table person (id integer, name string)");
            statement.executeUpdate("insert into person values(1, 'leo')");
            statement.executeUpdate("insert into person values(2, 'yui')");
            statement.executeUpdate("insert into person values(3, 'artem')");
            statement.executeUpdate("insert into person values(4, 'roma')");
            ResultSet rs = statement.executeQuery("select * from person");
            while (rs.next()) {
                // read the result set
                System.out.println("name = " + rs.getString("name"));
                System.out.println("id = " + rs.getInt("id"));
            }
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e);
            }
        }
    }

    public void runQuery(String name, String tag, Long townHall, Long versusTrophies, Long trophies) {
        Connection connection = null;

        try {
            // create a database connection
            connection = DriverManager.getConnection(PATH_TO_DB);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.

            statement.executeUpdate(String.format("insert into players values('%s', '%s', %d, %d, %d)", tag.substring(1), name, townHall, versusTrophies, trophies));

            log.info("select * from players");
            ResultSet rs = statement.executeQuery("select * from players");

            while (rs.next()) {
                // read the result set
                log.info("Name is " + rs.getString("name") + " / TH:" + rs.getInt("townhall") + " / Trophies(" + rs.getInt("versusTrophies") );
            }
        } catch (SQLException e) {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e);
            }
        }
    }
}