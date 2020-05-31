package com.home.server.db;

import com.home.server.dto.MembersData;
import com.home.server.dto.OneMember;
import com.home.server.dto.PlayerData;
import com.home.server.model.developer.CocToken;
import com.home.server.model.players.Players;
import com.home.server.service.AuthService;
import com.home.server.service.CocService;
import com.home.server.service.TelegramApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.logging.BotLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HerokuPostgresql {

    // Database credentials
    private static final String DB_URL = System.getenv("DB_URL");
    private static final String USER = System.getenv("DB_USER");
    private static final String PASS = System.getenv("DB_PASS");
    private static final Integer QUERY_TIMEOUT = 30; // seconds

    // COC service
    private static final String HOST = "https://api.clashofclans.com";
    private static final String COC_URL = System.getenv("COC_URL");
    private static final String COC_PASS = System.getenv("COC_PASS");
    private static final String COC_EMAIL = System.getenv("COC_EMAIL");
    private RestOperations restTemplate = new RestTemplate();
    private CocService cocService = new CocService(restTemplate, HOST);
    private AuthService authService = new AuthService(restTemplate, COC_URL);
    private TelegramApi telegramApi = new TelegramApi(restTemplate, "https://api.telegram.org/bot%s");

    private static final String LOGTAG = "CONNECTIONDB";

//    public HerokuPostgresql(IAuthService authService) {
//        this.authService = authService;
//    }

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
                    "tag_clan varchar (50), " +
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

    public String checkPlayerInDb(Players players, Boolean onlyChanges) {
        String playerName = "\uD83C\uDFAE" + players.getName() + "\n--------------------\n";
        String result = "";
        boolean state = false;
        Connection connection = null;

        try {
            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);

            Statement statement = connection.createStatement();
            log.debug("Check player with name : " + players.getName());

            PlayerData playerData = null;

            log.debug(" Player tag is : " + players.getTag());
            // If player exists in DB
            ResultSet rs = statement.executeQuery("select * from COC.PLR s WHERE s.tag = '" + players.getTag().substring(1) + "';");
            log.debug("TAG IS : " + players.getTag());

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

                log.debug(String.format("Игрок %s(%s) уже есть в БД, начинаем проверять его изменения...", players.getName(), players.getTag()));
                int trophiesDiff = players.getTrophies() - playerData.getTrophies();
                int vsTrophiesDiff = players.getVersusTrophies() - playerData.getVs_trophies();
                int thDiff = players.getTownHallLevel() - playerData.getTh();

                // Кубки в родной деревне
                if (trophiesDiff > 0) {
                    result += String.format("Home village +%d trophies.\n", trophiesDiff);
                } else if (trophiesDiff < 0) {
                    result += String.format("Home village %d trophies.\n", trophiesDiff);
                }

                // Кубки в в деревне строителя
                if (vsTrophiesDiff > 0) {
                    result += String.format("Builder base +%d trophies\n", vsTrophiesDiff);
                } else if ((vsTrophiesDiff < 0)) {
                    result += String.format("Builder base %d trophies\n", vsTrophiesDiff);
                }

                if (thDiff > 0) {
                    result += String.format("Up TH level: %d\n", players.getTownHallLevel());
                }

                // Update table
                statement.executeUpdate(String.format("UPDATE COC.PLR s SET trophies = %d, vs_trophies = %d, th = %d where s.tag = '%s'", players.getTrophies(), players.getVersusTrophies(), players.getTownHallLevel(), players.getTag().substring(1)));
//                statement.execute("commit");

                if (!result.equals("")) {
                    //result += String.format("\n\n%s\n", playerName);
                    result += String.format("--------------------");
                    result += String.format("\n\uD83C\uDFC6(HV:%d | BB:%d)", players.getTrophies(), players.getVersusTrophies(), players.getTownHallLevel());
                    playerName += result;
                    result = playerName;
                }
            }

            if (!state) {
                log.debug("\n\tЭтого игрока еще нет в БД, начинаем добавлять...");
                statement.executeUpdate(String.format("insert into COC.PLR(username, tag, tag_clan, trophies, vs_trophies, th) values('%s', '%s', '%s', %d, %d, %d)", players.getName(), players.getTag().substring(1), players.getClan().getTag().substring(1), players.getTrophies(), players.getVersusTrophies(), players.getTownHallLevel()));
                result += String.format("Игрок %s добавлен в БД как новый", players.getName());
            }


        } catch (SQLException e) {
            BotLogger.error(LOGTAG + " checkPlayerIn DB ", e.getMessage());
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
        if (!onlyChanges && result.equals("")) {
            result += String.format("%s\n\n\uD83C\uDFC6(HV:%d | BB:%d)", players.getName(), players.getTrophies(), players.getVersusTrophies());
        }
        return result;
    }

    public MembersData checkClanMembers(String clanTag) {
        MembersData result = new MembersData();
        List<OneMember> oneMemberList = new ArrayList<>();

        Connection connection = null;

        try {
            connection = DriverManager
                    .getConnection(DB_URL, USER, PASS);

            Statement statement = connection.createStatement();

            ResultSet rs = statement.executeQuery("select * from COC.PLR s WHERE s.tag_clan = '" + clanTag + "';");

            while (rs.next()) {
                log.debug("Read data from query...");
                CocToken cocToken = authService.getCocToken(COC_EMAIL, COC_PASS);
                Players players = cocService.getPlayers(cocToken, rs.getString("tag"));
                String playerInfo = checkPlayerInDb(players, true);

                log.debug("\n\t = = = = = = = = = Get data about player : " + players.getName());
                if (!playerInfo.equals(""))
                    oneMemberList.add(new OneMember(players.getName(), playerInfo));

                log.debug("Player name : " + players.getName());
                log.debug("Player info : " + playerInfo);
            }

        } catch (SQLException e) {
            BotLogger.error(LOGTAG + " checkClanMembers DB ", e.getMessage());
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
        result.setOneMemberList(oneMemberList);
        return result;
    }

    public void logInsert(String chatId, String message) {
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

            log.debug("Insert into LOG_MESSAGE...");

            statement.executeUpdate("insert into COC.LOG_MESSAGE(chat_id, message) values('" + chatId + "', '" + message + "')");

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
}