package com.kaptyuk.artem.base.service;

import com.kaptyuk.artem.base.model.MyIp;
import com.kaptyuk.artem.base.model.Token;
import com.kaptyuk.artem.base.model.locations.LocationId;
import com.kaptyuk.artem.base.model.players.Players;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestOperations;

@Slf4j
public class CocService extends BaseService implements ICocService {
    private static final String URL_LOCATIONS = "/v1/locations/";
    private static final String URL_PLAYERS = "/v1/players/";
    private static final String HOST = "https://api.clashofclans.com";

    public CocService(RestOperations restTemplate, String host) {
        super(restTemplate, host);
    }

    @Override
    public Players getPlayers(Token token, String playerId) {
        log.info("Loaded info about player tag : " + playerId);

        String url = prepareUrl(HOST, URL_PLAYERS, playerId);
        HttpHeaders httpHeaders = prepareRequestHeaders(token);
        httpHeaders.add("Authorization", "Bearer " + token.getAccess_token());

        log.info("Headers : " + prepareRequestHeaders(token));
        return request(url, HttpMethod.GET, httpHeaders, Players.class);
    }

    @Override
    public LocationId getLocations(Token token, String locationId) {
        log.info("Loaded info about locations id : " + locationId);
        String url = prepareUrl(HOST, URL_LOCATIONS, locationId);

        HttpHeaders httpHeaders = prepareRequestHeaders(token);
        httpHeaders.add("Authorization", "Bearer " + token.getAccess_token());

        log.info("Headers : " + prepareRequestHeaders(token));
        return request(url, HttpMethod.GET, httpHeaders, LocationId.class);
    }

    @Override
    public MyIp getMyIp(Token token) {
        log.info("Get my ip address");
        String url = prepareUrl("https://api.ipify.org", "?format=json", "");
        String url2 = prepareUrl("https://api.telegram.org", "/bot815044037:AAEpHnZwAzWSfl8hJ2aUybaPmr6aCLe87FQ/getMe", "");
        String url3 = prepareUrl("https://postman-echo.com", "/ip", "");


        return request(url3, HttpMethod.GET, prepareRequestHeaders(token), MyIp.class);
    }

    private String prepareUrl(String host, String url, String id) {
        log.info("URI : " + String.format("%s%s%s", host, url, id));
        return String.format("%s%s%s", host, url, id);
    }
}
