package com.home.server.db;

import com.home.server.dto.PlayerData;
import com.home.server.model.players.Players;
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
                    "tag_clan varchar (50) unique, " +
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
                log.error(String.format("SQL exception : %s", e.getMessage()));
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

    public String checkPlayerInDb(Players players) {
        String result = "";
        boolean state = false;
        Connection connection = null;

        try {
            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);

            Statement statement = connection.createStatement();
            log.debug("Check player with tag : " + players.getTag());

            PlayerData playerData = null;

            // If player exists in DB
            ResultSet rs = statement.executeQuery("select * from COC.PLR s WHERE s.tag = '" + players.getTag() + "';");
            while (rs.next()) {
                log.debug("select * from COC.PLR s WHERE s.tag = '" + players.getTag() + "'");
                if (rs.getString("tag") != null) {
                    state = true;
                    playerData = new PlayerData(rs.getInt("plr_id"),
                            rs.getString("username"),
                            rs.getString("tag"),
                            rs.getString("tag_clan"),
                            rs.getInt("trophies"),
                            rs.getInt("vs_trophies"),
                            rs.getInt("th"));
                }
            }

            if (state) {
                log.debug("\n\tЭтот игрок уже есть в БД, начинаем проверять его изменения...");
                int trophiesDiff = players.getTrophies() - playerData.getTrophies();
                int vsTrophiesDiff = players.getVersusTrophies() - playerData.getVs_trophies();
                int thDiff = players.getTownHallLevel() - playerData.getTh();

                // Кубки в родной деревне
                if (trophiesDiff > 0) {
                    result += String.format("%s получил кубки в родной деревне: %d (сейчас их %d) \n", players.getName(), trophiesDiff, players.getTrophies());
                } else if (trophiesDiff < 0) {
                    result += String.format("%s потерял кубки в родной деревне: %d (сейчас их %d) \n", players.getName(), trophiesDiff, players.getTrophies());
                }

                // Кубки в в деревне строителя
                if (vsTrophiesDiff > 0) {
                    result += String.format("%s получил кубки в деревне строителя: %d (сейчас их %d) \n", players.getName(), vsTrophiesDiff, players.getVersusTrophies());
                } else if ((vsTrophiesDiff < 0)) {
                    result += String.format("%s потерял кубки в деревне строителя: %d (сейчас их %d) \n", players.getName(), vsTrophiesDiff, players.getVersusTrophies());
                }

                if (thDiff > 0) {
                    result += String.format("%s поднял уровень ратуши до: %d \n", players.getName(), players.getTownHallLevel());
                }

                // Update table
                statement.executeUpdate(String.format("UPDATE COC.PLR s SET trophies = %d, vs_trophies = %d, th = %d where s.tag = '%s'", players.getTrophies(), players.getVersusTrophies(), players.getTownHallLevel(), players.getTag()));
//                statement.execute("commit");
            }

            if (!state) {
                log.debug("\n\tЭтого игрока еще нет в БД, начинаем добавлять...");
                statement.executeUpdate(String.format("insert into COC.PLR(username, tag, tag_clan, trophies, vs_trophies, th) values('%s', '%s', '%s', %d, %d, %d)", players.getName(), players.getTag().substring(1), players.getClan().getTag().substring(1), players.getTrophies(), players.getVersusTrophies(), players.getTownHallLevel()));
                result += String.format("Игрок %s добавлен в БД как новый", players.getName());
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
        if(result.equals(""))
            result = String.format("Нет изменений, у %s все по старому. (%d / %d)", players.getName(), players.getTrophies(), players.getVersusTrophies());
        return result;
    }
}
