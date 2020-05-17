package com.home.server.model.developer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Developer {
    private String id;
    private String name;
    private String game;
    private String email;
    private String tier;

    private String prevLoginTs;
    private String prevLoginIp;
    private String prevLoginUa;
}
