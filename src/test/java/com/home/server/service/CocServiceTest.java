package com.home.server.service;

import com.home.server.model.ListResult;
import com.home.server.model.MyIp;
import com.home.server.model.Token;
import com.home.server.model.locations.LocationId;
import com.home.server.model.members.MembersCommon;
import com.home.server.model.players.Players;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertNotNull;

@Slf4j
public class CocServiceTest {

    private RestOperations restTemplate = new RestTemplate();
    private CocService cocService = new CocService(restTemplate, host);
    private static final String host = "https://api.clashofclans.com";
    private static final String TOKEN = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6Ijc2MjMyYjM2LTM1NWYtNGZmMS05YjFmLWJkNDQ4MTRiYjMxYyIsImlhdCI6MTU4OTU2NzU3OCwic3ViIjoiZGV2ZWxvcGVyL2M4NTEzYmQyLTdiMGMtMjY4ZC01NTJjLWI0MjZjZTc2NjJhZSIsInNjb3BlcyI6WyJjbGFzaCJdLCJsaW1pdHMiOlt7InRpZXIiOiJkZXZlbG9wZXIvc2lsdmVyIiwidHlwZSI6InRocm90dGxpbmcifSx7ImNpZHJzIjpbIjkxLjI0MC4xMjQuMTMwIl0sInR5cGUiOiJjbGllbnQifV19.oqrDo7csJ6eory66UfgBmSqvbesqk-hPJT8ngM7euh7L6lTjJdhF_mFxYdp9jE3hIrSH_24QqQ3Fe8so8_zOJw";

    @Test
    public void getPlayers() {
        Token token = new Token();
        token.setAccess_token(TOKEN);
        Players players = cocService.getPlayers(token, "LCCCGJQYL");
        assertNotNull(players);
    }

    @Test
    public void getMembers(){
        Token token = new Token();
        token.setAccess_token(TOKEN);
        ListResult<MembersCommon> membersCommons = cocService.getMembers(token, "28VQVLVJO");
        System.out.println(membersCommons);
    }

    @Test
    public void getLocation() {
        Token token = new Token();
        token.setAccess_token(TOKEN);

        LocationId locationId = cocService.getLocations(token, "32000193");
        log.info("LOCATION IDS: " + locationId);
        assertNotNull(token);
    }

    @Test
    public void getMyIp() {
        Token token = new Token();

        MyIp myIp = cocService.getMyIp(token);
        log.info("\n\tMy IP is : " + myIp);
    }
}