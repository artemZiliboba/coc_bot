package com.home.server.service;

import com.home.server.db.HerokuPostgresql;
import com.home.server.model.players.Players;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

@Slf4j
public class DbTest {
    private HerokuPostgresql herokuSql = new HerokuPostgresql();

    @Test
    public void checkPlayerTest(){
        Players players = new Players();
        players.setName("8VP9RGVVQ");
        players.setTag("#8VP9RGVVQ");
        players.setTrophies(2670);
        players.setVersusTrophies(4270);
        players.setTownHallLevel(13);

        String result = herokuSql.checkPlayerInDb(players);
        log.debug("Result : " + result);
    }
}
