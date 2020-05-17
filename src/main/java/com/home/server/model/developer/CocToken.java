package com.home.server.model.developer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CocToken {
    private Status status;
    private Integer sessionExpiresInSeconds;
    private Auth auth;
    private Developer developer;
    private String temporaryAPIToken;
    private String swaggerUrl;
}
