package com.home.server.service;

import com.home.server.model.ListResult;
import com.home.server.model.MyIp;
import com.home.server.model.developer.CocToken;
import com.home.server.model.locations.LocationId;
import com.home.server.model.members.MembersCommon;
import com.home.server.model.players.Players;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestOperations;

@Slf4j
public class CocService extends BaseService implements ICocService {
    private static final String URL_LOCATIONS = "/v1/locations/";
    private static final String URL_MEMBERS = "/v1/clans/%s/members/";
    private static final String URL_PLAYERS = "/v1/players/";
    private static final String HOST = "https://api.clashofclans.com";

    public CocService(RestOperations restTemplate, String host) {
        super(restTemplate, host);
    }

    @Override
    public Players getPlayers(CocToken token, String playerId) {
        log.info("Loaded info about player tag : " + playerId.substring(1));

        String url = prepareUrl(HOST, URL_PLAYERS, "\u2116" + playerId);
        //HttpHeaders httpHeaders = prepareRequestHeaders(token);
        //httpHeaders.add("Authorization", "Bearer " + token.getAccess_token());

        log.info("Headers : " + prepareRequestHeaders(token));
        return request(url, HttpMethod.GET, prepareRequestHeaders(token), Players.class);
    }

    @Override
    public ListResult<MembersCommon> getMembers(CocToken token, String clanTag) {
        log.info("Loaded info about clan with tag : " + clanTag.substring(1));
        String url = prepareUrlOther(HOST, URL_MEMBERS, "\u2116" + clanTag);
        log.debug("URl : " + url);

        //HttpHeaders httpHeaders = prepareRequestHeaders(token);
        //httpHeaders.add("Authorization", "Bearer " + token.getAccess_token());

        return requestList(url, HttpMethod.GET, prepareRequestHeaders(token), MembersCommon.class);
    }

    @Override
    public LocationId getLocations(CocToken token, String locationId) {
        log.info("Loaded info about locations id : " + locationId);
        String url = prepareUrl(HOST, URL_LOCATIONS, locationId);

        //HttpHeaders httpHeaders = prepareRequestHeaders(token);
        //httpHeaders.add("Authorization", "Bearer " + token.getAccess_token());

        log.info("Headers : " + prepareRequestHeaders(token));
        return request(url, HttpMethod.GET, prepareRequestHeaders(token), LocationId.class);
    }

    @Override
    public MyIp getMyIp() {
        log.info("Get my ip address");
        String url = prepareUrl("https://postman-echo.com", "/ip", "");
        return request(url, HttpMethod.GET, null, MyIp.class);
    }

    private String prepareUrl(String host, String url, String id) {
        log.info("URI : " + String.format("%s%s%s", host, url, id));
        return String.format("%s%s%s", host, url, id);
    }

    private String prepareUrlOther(String host, String url, String id) {
        return host + String.format(url, id);
    }
}
