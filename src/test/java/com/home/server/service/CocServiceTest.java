package com.home.server.service;

import com.home.server.model.ListResult;
import com.home.server.model.MyIp;
import com.home.server.model.developer.CocToken;
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

    private static final String COC_URL = System.getenv("COC_URL");
    private static final String COC_EMAIL = System.getenv("COC_EMAIL");
    private static final String COC_PASS = System.getenv("COC_PASS");
    private AuthService authService;

    private RestOperations restTemplate = new RestTemplate();
    private CocService cocService = new CocService(restTemplate, COC_URL);


    public CocServiceTest() {
        authService = new AuthService(restTemplate, COC_URL);
    }

    @Test
    public void getCocToken() {

        CocToken cocToken = authService.getCocToken(COC_EMAIL, COC_PASS);
        System.out.println(cocToken.getTemporaryAPIToken());
    }

    @Test
    public void getPlayers() {
        CocToken cocToken = authService.getCocToken(COC_EMAIL, COC_PASS);

        Players players = cocService.getPlayers(cocToken, "LCCCGJQYL");
        assertNotNull(players);
    }

    @Test
    public void getMembers() {
        CocToken cocToken = authService.getCocToken(COC_EMAIL, COC_PASS);
        ListResult<MembersCommon> membersCommons = cocService.getMembers(cocToken, "28VQVLVJO");
        assertNotNull(membersCommons);
    }

    @Test
    public void getLocation() {
        CocToken cocToken = authService.getCocToken(COC_EMAIL, COC_PASS);

        LocationId locationId = cocService.getLocations(cocToken, "32000193");
        log.info("LOCATION IDS: " + locationId);
        assertNotNull(locationId);
    }

    @Test
    public void getMyIp() {
        MyIp myIp = cocService.getMyIp();
        log.info("\n\tMy IP is : " + myIp);
    }
}